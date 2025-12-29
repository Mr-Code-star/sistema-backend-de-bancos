package com.example.sistemabackenddebancos.accounts.domain.model.commands;

import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountId;

import java.math.BigDecimal;

public record DepositCommand(
        AccountId accountId,
        BigDecimal amount
) {}