package com.example.sistemabackenddebancos.payments.interfaces.rest.dtos.responses;

public record MerchantPayResponse(
        String status,      // SUCCESS / FAILED
        String reason       // optional
) {}
