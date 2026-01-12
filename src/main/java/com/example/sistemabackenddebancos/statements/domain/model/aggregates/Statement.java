package com.example.sistemabackenddebancos.statements.domain.model.aggregates;

import com.example.sistemabackenddebancos.statements.domain.model.valueobjects.DateRange;
import com.example.sistemabackenddebancos.statements.domain.model.valueobjects.StatementLine;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record Statement(
        UUID accountId,
        DateRange range,
        BigDecimal openingBalance,
        BigDecimal totalCredits,
        BigDecimal totalDebits,
        BigDecimal closingBalance,
        String currency,
        List<StatementLine> lines
) {}