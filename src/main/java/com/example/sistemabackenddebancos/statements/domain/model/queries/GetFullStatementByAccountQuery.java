package com.example.sistemabackenddebancos.statements.domain.model.queries;


import java.util.UUID;

public record GetFullStatementByAccountQuery(UUID accountId) {}