package com.example.sistemabackenddebancos.ledger.infrastructure.persistence.jpa.adapters;

import com.example.sistemabackenddebancos.ledger.domain.model.aggregates.LedgerEntry;
import com.example.sistemabackenddebancos.ledger.domain.model.valueobjects.TransactionReference;
import com.example.sistemabackenddebancos.ledger.domain.repositories.LedgerRepository;
import com.example.sistemabackenddebancos.ledger.infrastructure.persistence.jpa.mappers.LedgerPersistenceMapper;
import com.example.sistemabackenddebancos.ledger.infrastructure.persistence.jpa.repositories.SpringDataLedgerJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public class JpaLedgerRepositoryAdapter implements LedgerRepository {

    private final SpringDataLedgerJpaRepository jpa;

    public JpaLedgerRepositoryAdapter(SpringDataLedgerJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public LedgerEntry save(LedgerEntry entry) {
        var saved = jpa.save(LedgerPersistenceMapper.toEntity(entry));
        return LedgerPersistenceMapper.toDomain(saved);
    }

    @Override
    public List<LedgerEntry> findAllByAccountId(UUID accountId) {
        return jpa.findAllByAccountIdOrderByCreatedAtDesc(accountId)
                .stream()
                .map(LedgerPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<LedgerEntry> findAllByReference(TransactionReference reference) {
        return jpa.findAllByReferenceOrderByCreatedAtDesc(reference.value())
                .stream()
                .map(LedgerPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<LedgerEntry> findAllByAccountIdBetween(UUID accountId, Instant fromExclusive, Instant toInclusive) {
        return jpa.findAllByAccountIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
                        accountId, fromExclusive, toInclusive)
                .stream()
                .map(LedgerPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<LedgerEntry> findAllByAccountIdBefore(UUID accountId, Instant beforeInclusive) {
        return jpa.findAllByAccountIdAndCreatedAtLessThanOrderByCreatedAtDesc(accountId, beforeInclusive)
                .stream()
                .map(LedgerPersistenceMapper::toDomain)
                .toList();
    }
}