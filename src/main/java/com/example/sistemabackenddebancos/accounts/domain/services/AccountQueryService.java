package com.example.sistemabackenddebancos.accounts.domain.services;

import com.example.sistemabackenddebancos.accounts.domain.model.aggregates.BankAccount;
import com.example.sistemabackenddebancos.accounts.domain.model.queries.GetAccountBalanceQuery;
import com.example.sistemabackenddebancos.accounts.domain.model.queries.GetAccountByIdQuery;
import com.example.sistemabackenddebancos.accounts.domain.model.queries.GetAccountByNumberQuery;
import com.example.sistemabackenddebancos.accounts.domain.model.queries.GetAccountsByOwnerIdQuery;
import com.example.sistemabackenddebancos.shared.domain.model.valueobjects.Money;

import java.util.List;
import java.util.Optional;

public interface AccountQueryService {

    Optional<BankAccount> handle(GetAccountByIdQuery query);
    Optional<BankAccount> handle(GetAccountByNumberQuery query);

    List<BankAccount> handle(GetAccountsByOwnerIdQuery query);

    Optional<Money> handle(GetAccountBalanceQuery query);
}