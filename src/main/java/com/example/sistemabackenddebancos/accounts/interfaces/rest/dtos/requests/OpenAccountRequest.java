package com.example.sistemabackenddebancos.accounts.interfaces.rest.dtos.requests;

public record OpenAccountRequest(
        String type,      // SAVINGS / CHECKING
        String currency   // PEN / USD
) {}