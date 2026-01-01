package com.example.sistemabackenddebancos.transfers.application.services;

import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountId;
import com.example.sistemabackenddebancos.accounts.domain.repositories.AccountRepository;
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

    public TransferCommandServiceImpl(TransferRepository transferRepository,
                                      AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
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

        try {
            // 3) Validaciones + cargar cuentas
            UUID fromId = command.fromAccountId();
            UUID toId = command.toAccountId();

            var fromOpt = accountRepository.findById(new AccountId(fromId));
            var toOpt = accountRepository.findById(new AccountId(toId));

            if (fromOpt.isEmpty() || toOpt.isEmpty()) {
                transfer = transfer.markFailed("Account not found");
                return Optional.of(transferRepository.save(transfer));
            }

            var from = fromOpt.get();
            var to = toOpt.get();

            // Validación moneda (transferencia interna misma moneda)
            if (from.currency() != command.currency() || to.currency() != command.currency()) {
                transfer = transfer.markFailed("Currency mismatch");
                return Optional.of(transferRepository.save(transfer));
            }

            // 4) Ejecutar movimientos (validaciones de estado/saldo ya están en el aggregate)
            var updatedFrom = from.withdraw(command.amount());
            var updatedTo = to.deposit(command.amount());

            accountRepository.save(updatedFrom);
            accountRepository.save(updatedTo);

            // 5) Completar transferencia
            transfer = transfer.markCompleted();
            return Optional.of(transferRepository.save(transfer));

        } catch (Exception ex) {
            // Si algo falla: marcar FAILED
            if (transfer.status() == TransferStatus.PENDING) {
                transfer = transfer.markFailed(ex.getMessage());
                transfer = transferRepository.save(transfer);
            }
            return Optional.of(transfer);
        }
    }
}