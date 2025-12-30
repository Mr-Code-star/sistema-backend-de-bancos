package com.example.sistemabackenddebancos.accounts.applications.services;

import com.example.sistemabackenddebancos.accounts.applications.generators.AccountNumberGenerator;
import com.example.sistemabackenddebancos.accounts.domain.model.aggregates.BankAccount;
import com.example.sistemabackenddebancos.accounts.domain.model.commands.*;
import com.example.sistemabackenddebancos.accounts.domain.repositories.AccountRepository;
import com.example.sistemabackenddebancos.accounts.domain.services.AccountCommandService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountCommandServiceImpl implements AccountCommandService {

    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountCommandServiceImpl(AccountRepository accountRepository,
                                     AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository = accountRepository;
        this.accountNumberGenerator = accountNumberGenerator;
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
    public Optional<BankAccount> handle(DepositCommand command) {
        var accountOpt = accountRepository.findById(command.accountId());
        if (accountOpt.isEmpty()) return Optional.empty();

        var updated = accountOpt.get().deposit(command.amount());
        return Optional.of(accountRepository.save(updated));
    }

    @Override
    public Optional<BankAccount> handle(WithdrawCommand command) {
        var accountOpt = accountRepository.findById(command.accountId());
        if (accountOpt.isEmpty()) return Optional.empty();

        var updated = accountOpt.get().withdraw(command.amount());
        return Optional.of(accountRepository.save(updated));
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
