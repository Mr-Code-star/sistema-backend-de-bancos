package com.example.sistemabackenddebancos.transfers.application.services;

import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountId;
import com.example.sistemabackenddebancos.accounts.domain.repositories.AccountRepository;
import com.example.sistemabackenddebancos.ledger.domain.model.commands.PostLedgerEntryCommand;
import com.example.sistemabackenddebancos.ledger.domain.model.enumerations.EntrySource;
import com.example.sistemabackenddebancos.ledger.domain.model.enumerations.EntryType;
import com.example.sistemabackenddebancos.ledger.domain.model.valueobjects.TransactionReference;
import com.example.sistemabackenddebancos.ledger.domain.services.LedgerCommandService;
import com.example.sistemabackenddebancos.profile.application.services.NotificationOrchestrator;
import com.example.sistemabackenddebancos.transfers.domain.model.aggregates.Transfer;
import com.example.sistemabackenddebancos.transfers.domain.model.commands.CreateTransferCommand;
import com.example.sistemabackenddebancos.transfers.domain.model.enumerations.TransferStatus;
import com.example.sistemabackenddebancos.transfers.domain.repositories.TransferRepository;
import com.example.sistemabackenddebancos.transfers.domain.services.TransferCommandService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TransferCommandServiceImpl implements TransferCommandService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final LedgerCommandService ledgerCommandService;
    private final NotificationOrchestrator notificationOrchestrator;

    public TransferCommandServiceImpl(TransferRepository transferRepository,
                                      AccountRepository accountRepository, LedgerCommandService ledgerCommandService, NotificationOrchestrator notificationOrchestrator) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
        this.ledgerCommandService = ledgerCommandService;
        this.notificationOrchestrator = notificationOrchestrator;
    }

    @Override
    @Transactional
    public Optional<Transfer> handle(CreateTransferCommand command) {

        // 1) Idempotency: si ya existe, devolvemos el mismo resultado
        var existing = transferRepository.findByReference(command.reference());
        if (existing.isPresent()) {
            return existing;
        }

        // 2) Crear transferencia PENDING
        var transfer = Transfer.createNew(
                command.reference(),
                command.fromAccountId(),
                command.toAccountId(),
                command.currency(),
                command.amount()
        );

        // Guardamos PENDING (útil para auditoría si algo falla)
        transfer = transferRepository.save(transfer);

        UUID fromOwner = null;
        UUID toOwner = null;
        String transferRef = command.reference().value();

        try {
            // 3) Validaciones + cargar cuentas
            UUID fromId = command.fromAccountId();
            UUID toId = command.toAccountId();

            var fromOpt = accountRepository.findById(new AccountId(fromId));
            var toOpt = accountRepository.findById(new AccountId(toId));

            if (fromOpt.isEmpty() || toOpt.isEmpty()) {
                transfer = transfer.markFailed("Account not found");
                transfer = transferRepository.save(transfer);

                // Notificar al emisor si podemos identificarlo
                if (fromOpt.isPresent()) {
                    fromOwner = fromOpt.get().ownerId().value();
                    notificationOrchestrator.notifyTransfer(
                            fromOwner,
                            "Transfer failed",
                            "Your transfer failed: Account not found",
                            transferRef
                    );
                }

                return Optional.of(transfer);
            }

            var from = fromOpt.get();
            var to = toOpt.get();

            fromOwner = from.ownerId().value();
            toOwner = to.ownerId().value();

            // Validación moneda (transferencia interna misma moneda)
            if (from.currency() != command.currency() || to.currency() != command.currency()) {
                transfer = transfer.markFailed("Currency mismatch");
                transfer = transferRepository.save(transfer);

                notificationOrchestrator.notifyTransfer(
                        fromOwner,
                        "Transfer failed",
                        "Your transfer failed: Currency mismatch",
                        transferRef
                );

                return Optional.of(transfer);
            }

            // 4) Ejecutar movimientos (validaciones de estado/saldo ya están en el aggregate)
            var updatedFrom = from.withdraw(command.amount());
            var updatedTo = to.deposit(command.amount());

            accountRepository.save(updatedFrom);
            accountRepository.save(updatedTo);

            // 5) Completar transferencia
            transfer = transfer.markCompleted();
            transfer = transferRepository.save(transfer);

            // ✅ LEDGER ENTRIES (TRANSFER)
            // usamos la misma reference para correlación contable
            var ref = new TransactionReference(command.reference().value());

            // DEBIT en cuenta origen
            ledgerCommandService.handle(new PostLedgerEntryCommand(
                    fromId,
                    EntryType.DEBIT,
                    EntrySource.TRANSFER,
                    command.currency(),
                    command.amount(),
                    ref
            ));

            // CREDIT en cuenta destino
            ledgerCommandService.handle(new PostLedgerEntryCommand(
                    toId,
                    EntryType.CREDIT,
                    EntrySource.TRANSFER,
                    command.currency(),
                    command.amount(),
                    ref
            ));

            // ✅ NOTIFICATIONS (según preferencias del usuario: IN_APP/EMAIL/SMS)
            notificationOrchestrator.notifyTransfer(
                    fromOwner,
                    "Transfer sent",
                    "You sent " + command.amount().toPlainString() + " " + command.currency().name()
                            + " to account " + to.accountNumber().value(),
                    transferRef
            );

            notificationOrchestrator.notifyTransfer(
                    toOwner,
                    "Transfer received",
                    "You received " + command.amount().toPlainString() + " " + command.currency().name()
                            + " from account " + from.accountNumber().value(),
                    transferRef
            );

            return Optional.of(transfer);
        } catch (Exception ex) {
            // Si algo falla: marcar FAILED
            if (transfer.status() == TransferStatus.PENDING) {
                transfer = transfer.markFailed(ex.getMessage());
                transfer = transferRepository.save(transfer);
            }

            // Notificar al emisor si ya lo conocemos
            if (fromOwner != null) {
                notificationOrchestrator.notifyTransfer(
                        fromOwner,
                        "Transfer failed",
                        "Your transfer failed: " + (transfer.failureReason() != null ? transfer.failureReason() : ex.getMessage()),
                        transferRef
                );
            }

            return Optional.of(transfer);
        }
    }
}