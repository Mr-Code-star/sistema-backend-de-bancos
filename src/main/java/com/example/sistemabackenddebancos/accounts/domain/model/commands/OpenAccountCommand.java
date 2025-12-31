package com.example.sistemabackenddebancos.accounts.domain.model.commands;

import com.example.sistemabackenddebancos.accounts.domain.model.enumerations.AccountType;
import com.example.sistemabackenddebancos.shared.domain.model.enumerations.Currency;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.OwnerId;

public record OpenAccountCommand(
        OwnerId ownerId,
        AccountType type,
        Currency currency
) {}