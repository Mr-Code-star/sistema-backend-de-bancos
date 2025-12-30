package com.example.sistemabackenddebancos.accounts.interfaces.rest.dtos.requests;

public record OpenAccountRequest(
        String ownerId,   // UUID del user (IAM)
        String type,      // SAVINGS / CHECKING
        String currency   // PEN / USD
) {}