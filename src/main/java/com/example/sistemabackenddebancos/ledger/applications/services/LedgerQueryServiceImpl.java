package com.example.sistemabackenddebancos.ledger.applications.services;

import com.example.sistemabackenddebancos.ledger.domain.model.aggregates.LedgerEntry;
import com.example.sistemabackenddebancos.ledger.domain.model.queries.GetEntriesByAccountIdQuery;
import com.example.sistemabackenddebancos.ledger.domain.model.queries.GetEntriesByReferenceQuery;
import com.example.sistemabackenddebancos.ledger.domain.repositories.LedgerRepository;
import com.example.sistemabackenddebancos.ledger.domain.services.LedgerQueryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LedgerQueryServiceImpl implements LedgerQueryService {

    private final LedgerRepository ledgerRepository;

    public LedgerQueryServiceImpl(LedgerRepository ledgerRepository) {
        this.ledgerRepository = ledgerRepository;
    }

    @Override
    public List<LedgerEntry> handle(GetEntriesByAccountIdQuery query) {
        return ledgerRepository.findAllByAccountId(query.accountId());
    }

    @Override
    public List<LedgerEntry> handle(GetEntriesByReferenceQuery query) {
        return ledgerRepository.findAllByReference(query.reference());
    }
}