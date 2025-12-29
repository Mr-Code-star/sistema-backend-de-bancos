package com.example.sistemabackenddebancos.accounts.domain.model.queries;

import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountId;

public record GetAccountBalanceQuery(AccountId accountId) {}
