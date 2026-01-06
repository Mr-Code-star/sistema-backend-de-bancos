package com.example.sistemabackenddebancos.payments.interfaces.rest.dtos.responses;

public record PaymentResponse(
        String paymentId,
        String reference,
        String fromAccountId,
        String merchantCode,
        String type,
        String currency,
        String amount,
        String status,
        String failureReason
) {}