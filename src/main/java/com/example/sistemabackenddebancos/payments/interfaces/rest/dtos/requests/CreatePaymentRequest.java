package com.example.sistemabackenddebancos.payments.interfaces.rest.dtos.requests;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        String reference,
        String fromAccountId,
        String merchantCode,
        String type,
        String currency,
        BigDecimal amount,
        String customerRef
) {
}
