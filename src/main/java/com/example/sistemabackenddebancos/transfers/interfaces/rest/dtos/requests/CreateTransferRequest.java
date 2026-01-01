package com.example.sistemabackenddebancos.transfers.interfaces.rest.dtos.requests;

import java.math.BigDecimal;

public record CreateTransferRequest(
        String reference,     // idempotency key (UUID recomendado)
        String fromAccountId, // UUID
        String toAccountId,   // UUID
        String currency,      // PEN/USD
        BigDecimal amount
) {}