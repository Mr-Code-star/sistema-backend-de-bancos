package com.example.sistemabackenddebancos.accounts.applications.services;

import com.example.sistemabackenddebancos.accounts.applications.generators.AccountNumberGenerator;
import com.example.sistemabackenddebancos.accounts.domain.model.aggregates.BankAccount;
import com.example.sistemabackenddebancos.accounts.domain.model.commands.*;
import com.example.sistemabackenddebancos.accounts.domain.repositories.AccountRepository;
import com.example.sistemabackenddebancos.accounts.domain.services.AccountCommandService;
import com.example.sistemabackenddebancos.ledger.domain.model.commands.PostLedgerEntryCommand;
import com.example.sistemabackenddebancos.ledger.domain.model.enumerations.EntrySource;
import com.example.sistemabackenddebancos.ledger.domain.model.enumerations.EntryType;
import com.example.sistemabackenddebancos.ledger.domain.model.valueobjects.TransactionReference;
import com.example.sistemabackenddebancos.ledger.domain.services.LedgerCommandService;
import com.example.sistemabackenddebancos.profile.application.services.NotificationOrchestrator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AccountCommandServiceImpl implements AccountCommandService {

    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final LedgerCommandService ledgerCommandService;
    private final NotificationOrchestrator notificationOrchestrator;

    public AccountCommandServiceImpl(AccountRepository accountRepository,
                                     AccountNumberGenerator accountNumberGenerator, LedgerCommandService ledgerCommandService, NotificationOrchestrator notificationOrchestrator) {
        this.accountRepository = accountRepository;
        this.accountNumberGenerator = accountNumberGenerator;
        this.ledgerCommandService = ledgerCommandService;
        this.notificationOrchestrator = notificationOrchestrator;
    }

    @Override
    public Optional<BankAccount> handle(OpenAccountCommand command) {

        // Genera un número único (intenta varias veces para evitar colisiones)
        BankAccount created = null;
        for (int i = 0; i < 10; i++) {
            var number = accountNumberGenerator.generate();
            if (!accountRepository.existsByAccountNumber(number)) {
                created = BankAccount.open(command.ownerId(), number, command.type(), command.currency());
                break;
            }
        }

        if (created == null) return Optional.empty();

        return Optional.of(accountRepository.save(created));
    }

    @Override
    @Transactional
    public Optional<BankAccount> handle(DepositCommand command) {
        var accountOpt = accountRepository.findById(command.accountId());
        if (accountOpt.isEmpty()) return Optional.empty();

        var updated = accountOpt.get().deposit(command.amount());
        updated = accountRepository.save(updated);

        // Ledger CREDIT
        var ref = new TransactionReference(UUID.randomUUID().toString()); // reference única para esta operación
        ledgerCommandService.handle(new PostLedgerEntryCommand(
                updated.id().value(),
                EntryType.CREDIT,
                EntrySource.DEPOSIT,
                updated.currency(),
                command.amount(),
                ref
        ));

        UUID owner = updated.ownerId().value();
        notificationOrchestrator.notifyAccount(
                owner,
                "Deposit received",
                "Deposit of " + command.amount().toPlainString() + " " + updated.currency().name()
                        + " into account " + updated.accountNumber().value(),
                UUID.randomUUID().toString()
        );

        return Optional.of(updated);
    }

    @Override
    @Transactional
    public Optional<BankAccount> handle(WithdrawCommand command) {
        var accountOpt = accountRepository.findById(command.accountId());
        if (accountOpt.isEmpty()) return Optional.empty();

        var updated = accountOpt.get().withdraw(command.amount());
        updated = accountRepository.save(updated);

        // Ledger DEBIT
        var ref = new TransactionReference(UUID.randomUUID().toString());
        ledgerCommandService.handle(new PostLedgerEntryCommand(
                updated.id().value(),
                EntryType.DEBIT,
                EntrySource.WITHDRAW,
                updated.currency(),
                command.amount(),
                ref
        ));
        UUID owner = updated.ownerId().value();
        notificationOrchestrator.notifyAccount(
                owner,
                "Withdrawal made",
                "Withdrawal of " + command.amount().toPlainString() + " " + updated.currency().name()
                        + " from account " + updated.accountNumber().value(),
                UUID.randomUUID().toString()
        );

        return Optional.of(updated);
    }

    @Override
    public Optional<BankAccount> handle(FreezeAccountCommand command) {
        var accountOpt = accountRepository.findById(command.accountId());
        if (accountOpt.isEmpty()) return Optional.empty();

        var updated = accountOpt.get().freeze();
        return Optional.of(accountRepository.save(updated));
    }

    @Override
    public Optional<BankAccount> handle(UnfreezeAccountCommand command) {
        var accountOpt = accountRepository.findById(command.accountId());
        if (accountOpt.isEmpty()) return Optional.empty();

        var updated = accountOpt.get().unfreeze();
        return Optional.of(accountRepository.save(updated));
    }

    @Override
    public Optional<BankAccount> handle(CloseAccountCommand command) {
        var accountOpt = accountRepository.findById(command.accountId());
        if (accountOpt.isEmpty()) return Optional.empty();

        var updated = accountOpt.get().close();
        return Optional.of(accountRepository.save(updated));
    }
}
