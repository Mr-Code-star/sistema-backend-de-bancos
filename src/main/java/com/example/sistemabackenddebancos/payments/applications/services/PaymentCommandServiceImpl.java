package com.example.sistemabackenddebancos.payments.applications.services;

import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountId;
import com.example.sistemabackenddebancos.accounts.domain.repositories.AccountRepository;
import com.example.sistemabackenddebancos.ledger.domain.model.commands.PostLedgerEntryCommand;
import com.example.sistemabackenddebancos.ledger.domain.model.enumerations.EntrySource;
import com.example.sistemabackenddebancos.ledger.domain.model.enumerations.EntryType;
import com.example.sistemabackenddebancos.ledger.domain.model.valueobjects.TransactionReference;
import com.example.sistemabackenddebancos.ledger.domain.services.LedgerCommandService;
import com.example.sistemabackenddebancos.payments.applications.integrations.MerchantGateway;
import com.example.sistemabackenddebancos.payments.domain.model.aggregates.Payment;
import com.example.sistemabackenddebancos.payments.domain.model.commands.CreatePaymentCommand;
import com.example.sistemabackenddebancos.payments.domain.model.enumerations.PaymentStatus;
import com.example.sistemabackenddebancos.payments.domain.repositories.PaymentRepository;
import com.example.sistemabackenddebancos.payments.domain.services.PaymentCommandService;
import com.example.sistemabackenddebancos.profile.application.services.NotificationOrchestrator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentCommandServiceImpl implements PaymentCommandService {

    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;
    private final MerchantGateway merchantGateway;
    private final LedgerCommandService ledgerCommandService;
    private final NotificationOrchestrator notificationOrchestrator;

    public PaymentCommandServiceImpl(PaymentRepository paymentRepository,
                                     AccountRepository accountRepository,
                                     MerchantGateway merchantGateway,
                                     LedgerCommandService ledgerCommandService,
                                     NotificationOrchestrator notificationOrchestrator) {
        this.paymentRepository = paymentRepository;
        this.accountRepository = accountRepository;
        this.merchantGateway = merchantGateway;
        this.ledgerCommandService = ledgerCommandService;
        this.notificationOrchestrator = notificationOrchestrator;
    }

    @Override
    @Transactional
    public Optional<Payment> handle(CreatePaymentCommand command) {

        // 1) Idempotency
        var existing = paymentRepository.findByReference(command.reference());
        if (existing.isPresent()) return existing;

        // 2) Crear Payment PENDING
        var payment = Payment.createNew(
                command.reference(),
                command.fromAccountId(),
                command.merchantCode(),
                command.type(),
                command.currency(),
                command.amount()
        );
        payment = paymentRepository.save(payment);

        // 3) Cargar cuenta (existencia + currency)
        var accOpt = accountRepository.findById(new AccountId(command.fromAccountId()));
        if (accOpt.isEmpty()) {
            payment = payment.markFailed("Account not found");
            payment = paymentRepository.save(payment);
            return Optional.of(payment);
        }

        var account = accOpt.get();

        // Validación moneda
        if (account.currency() != command.currency()) {
            payment = payment.markFailed("Currency mismatch");
            payment = paymentRepository.save(payment);
            notifyPaymentFailed(account.ownerId().value(), payment);
            return Optional.of(payment);
        }

        // 4) Llamar merchant externo (simulado)
        // customerRef: por ahora usamos accountNumber como referencia del pagador (puedes cambiarlo a serviceNumber)
        var merchantResult = merchantGateway.pay(
                command.merchantCode(),
                command.reference(),
                command.amount(),
                command.currency(),
                command.customerRef()
        );

        if (!merchantResult.success()) {
            payment = payment.markFailed(merchantResult.failureReason());
            payment = paymentRepository.save(payment);
            notifyPaymentFailed(account.ownerId().value(), payment);
            return Optional.of(payment);
        }

        // 5) SUCCESS => debitar cuenta (withdraw) + ledger DEBIT + payment COMPLETED
        try {
            var updatedAccount = account.withdraw(command.amount());
            accountRepository.save(updatedAccount);

            // Ledger DEBIT (PAYMENT)
            var ref = new TransactionReference(command.reference().value());
            ledgerCommandService.handle(new PostLedgerEntryCommand(
                    command.fromAccountId(),
                    EntryType.DEBIT,
                    EntrySource.PAYMENT,
                    command.currency(),
                    command.amount(),
                    ref
            ));

            payment = payment.markCompleted();
            payment = paymentRepository.save(payment);

            notifyPaymentCompleted(updatedAccount.ownerId().value(), payment, updatedAccount.accountNumber().value());

            return Optional.of(payment);

        } catch (Exception ex) {
            // Ej: insufficient funds
            if (payment.status() == PaymentStatus.PENDING) {
                payment = payment.markFailed(ex.getMessage());
                payment = paymentRepository.save(payment);
            }
            notifyPaymentFailed(account.ownerId().value(), payment);
            return Optional.of(payment);
        }
    }

    private void notifyPaymentCompleted(java.util.UUID userId, Payment payment, String accountNumber) {
        notificationOrchestrator.notifyAccount(
                userId,
                "Payment completed",
                "Payment to " + payment.merchantCode().value()
                        + " for " + payment.amount().toPlainString() + " " + payment.currency().name()
                        + " was completed (account " + accountNumber + ")",
                payment.reference().value()
        );
    }

    private void notifyPaymentFailed(java.util.UUID userId, Payment payment) {
        notificationOrchestrator.notifyAccount(
                userId,
                "Payment failed",
                "Payment to " + payment.merchantCode().value()
                        + " failed: " + (payment.failureReason() != null ? payment.failureReason() : "Unknown error"),
                payment.reference().value()
        );
    }
}