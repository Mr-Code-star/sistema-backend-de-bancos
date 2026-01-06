package com.example.sistemabackenddebancos.payments.interfaces.rest.dtos.requests;

import java.math.BigDecimal;

public record MerchantPayRequest(
        String merchantCode,
        String reference,
        String customerRef,
        String currency,
        BigDecimal amount
) {}