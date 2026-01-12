package com.example.sistemabackenddebancos.ledger.infrastructure.persistence.jpa.repositories;

import com.example.sistemabackenddebancos.ledger.infrastructure.persistence.jpa.entities.LedgerEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataLedgerJpaRepository extends JpaRepository<LedgerEntryEntity, UUID> {

    List<LedgerEntryEntity> findAllByAccountIdOrderByCreatedAtDesc(UUID accountId);

    List<LedgerEntryEntity> findAllByReferenceOrderByCreatedAtDesc(String reference);

    List<LedgerEntryEntity> findAllByAccountIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
            UUID accountId, Instant from, Instant to);

    List<LedgerEntryEntity> findAllByAccountIdAndCreatedAtLessThanOrderByCreatedAtDesc(
            UUID accountId, Instant before);

}