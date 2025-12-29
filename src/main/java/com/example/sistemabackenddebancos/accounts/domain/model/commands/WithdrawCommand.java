package com.example.sistemabackenddebancos.accounts.domain.model.commands;

import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountId;

import java.math.BigDecimal;

public record WithdrawCommand(
        AccountId accountId,
        BigDecimal amount
) {}