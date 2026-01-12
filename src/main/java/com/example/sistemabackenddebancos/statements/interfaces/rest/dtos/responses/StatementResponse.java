package com.example.sistemabackenddebancos.statements.interfaces.rest.dtos.responses;

import java.util.List;

public record StatementResponse(
        String accountId,
        String from,
        String to,
        String currency,
        String openingBalance,
        String totalCredits,
        String totalDebits,
        String closingBalance,
        List<StatementLineResponse> lines
) {

}
