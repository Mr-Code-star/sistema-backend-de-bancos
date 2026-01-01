package com.example.sistemabackenddebancos.ledger.domain.model.queries;


import java.util.UUID;

public record GetEntriesByAccountIdQuery(UUID accountId) {}