package com.example.sistemabackenddebancos.ledger.domain.model.queries;

import com.example.sistemabackenddebancos.ledger.domain.model.valueobjects.TransactionReference;

public record GetEntriesByReferenceQuery(TransactionReference reference) {}