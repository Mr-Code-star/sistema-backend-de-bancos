package com.example.sistemabackenddebancos.ledger.domain.services;

import com.example.sistemabackenddebancos.ledger.domain.model.aggregates.LedgerEntry;
import com.example.sistemabackenddebancos.ledger.domain.model.queries.*;

import java.util.List;

public interface LedgerQueryService {
    List<LedgerEntry> handle(GetEntriesByAccountIdQuery query);
    List<LedgerEntry> handle(GetEntriesByReferenceQuery query);
}