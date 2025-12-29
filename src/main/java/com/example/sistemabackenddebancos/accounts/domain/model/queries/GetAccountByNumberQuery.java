package com.example.sistemabackenddebancos.accounts.domain.model.queries;

import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountNumber;

public record GetAccountByNumberQuery(AccountNumber accountNumber) {}
