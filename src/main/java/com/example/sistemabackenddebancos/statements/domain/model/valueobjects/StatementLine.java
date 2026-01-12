package com.example.sistemabackenddebancos.statements.domain.model.valueobjects;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record StatementLine(
        Instant timestamp,
        UUID accountId,
        String type,
        String source,
        BigDecimal amount,
        String  currency,
        String reference
) {
}
