package com.example.sistemabackenddebancos.accounts.domain.model.commands;

import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountId;

public record CloseAccountCommand(AccountId accountId) {}