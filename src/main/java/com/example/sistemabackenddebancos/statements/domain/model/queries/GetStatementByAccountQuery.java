package com.example.sistemabackenddebancos.statements.domain.model.queries;

import com.example.sistemabackenddebancos.statements.domain.model.valueobjects.DateRange;

import java.util.UUID;

public record GetStatementByAccountQuery(UUID accountId, DateRange range) {
}
