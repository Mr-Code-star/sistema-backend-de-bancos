package com.example.sistemabackenddebancos.statements.interfaces.rest.dtos.responses;

public record StatementLineResponse(
        String timestamp,
        String type,
        String source,
        String amount,
        String currency,
        String reference
) {
}
