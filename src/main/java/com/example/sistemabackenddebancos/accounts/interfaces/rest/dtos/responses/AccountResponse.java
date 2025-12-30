package com.example.sistemabackenddebancos.accounts.interfaces.rest.dtos.responses;

public record AccountResponse(
        String accountId,
        String ownerId,
        String accountNumber,
        String type,
        String status,
        String currency,
        String balance
) {}