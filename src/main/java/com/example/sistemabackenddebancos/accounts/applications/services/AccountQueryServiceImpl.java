package com.example.sistemabackenddebancos.accounts.applications.services;

import com.example.sistemabackenddebancos.accounts.domain.model.aggregates.BankAccount;
import com.example.sistemabackenddebancos.accounts.domain.model.queries.*;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.Money;
import com.example.sistemabackenddebancos.accounts.domain.repositories.AccountRepository;
import com.example.sistemabackenddebancos.accounts.domain.services.AccountQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountQueryServiceImpl implements AccountQueryService {

    private final AccountRepository accountRepository;

    public AccountQueryServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<BankAccount> handle(GetAccountByIdQuery query) {
        return accountRepository.findById(query.accountId());
    }

    @Override
    public Optional<BankAccount> handle(GetAccountByNumberQuery query) {
        return accountRepository.findByAccountNumber(query.accountNumber());
    }

    @Override
    public List<BankAccount> handle(GetAccountsByOwnerIdQuery query) {
        return accountRepository.findAllByOwnerId(query.ownerId());
    }

    @Override
    public Optional<Money> handle(GetAccountBalanceQuery query) {
        return accountRepository.findById(query.accountId()).map(BankAccount::balance);
    }
}