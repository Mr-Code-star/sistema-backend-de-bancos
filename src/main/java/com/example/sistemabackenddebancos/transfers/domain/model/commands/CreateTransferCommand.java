package com.example.sistemabackenddebancos.transfers.domain.model.commands;

import com.example.sistemabackenddebancos.shared.domain.model.enumerations.Currency;
import com.example.sistemabackenddebancos.transfers.domain.model.valueobjects.TransferReference;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransferCommand(
        TransferReference reference,
        UUID fromAccountId,
        UUID toAccountId,
        Currency currency,
        BigDecimal amount
) {}