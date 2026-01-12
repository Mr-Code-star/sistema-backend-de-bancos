package com.example.sistemabackenddebancos.ledger.domain.repositories;

import com.example.sistemabackenddebancos.ledger.domain.model.aggregates.LedgerEntry;
import com.example.sistemabackenddebancos.ledger.domain.model.valueobjects.TransactionReference;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface LedgerRepository {

    LedgerEntry save(LedgerEntry entry);

    List<LedgerEntry> findAllByAccountId(UUID accountId);

    List<LedgerEntry> findAllByReference(TransactionReference reference);

    List<LedgerEntry> findAllByAccountIdBetween(UUID accountId, Instant fromExclusive, Instant toInclusive);

    List<LedgerEntry> findAllByAccountIdBefore(UUID accountId, Instant beforeInclusive);
}