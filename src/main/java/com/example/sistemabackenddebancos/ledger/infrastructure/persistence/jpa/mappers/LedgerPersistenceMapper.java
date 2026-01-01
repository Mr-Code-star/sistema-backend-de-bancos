package com.example.sistemabackenddebancos.ledger.infrastructure.persistence.jpa.mappers;

import com.example.sistemabackenddebancos.ledger.domain.model.aggregates.LedgerEntry;
import com.example.sistemabackenddebancos.ledger.domain.model.valueobjects.EntryId;
import com.example.sistemabackenddebancos.ledger.domain.model.valueobjects.TransactionReference;
import com.example.sistemabackenddebancos.ledger.infrastructure.persistence.jpa.entities.LedgerEntryEntity;

public class LedgerPersistenceMapper {

    public static LedgerEntry toDomain(LedgerEntryEntity e) {
        return new LedgerEntry(
                new EntryId(e.getId()),
                e.getAccountId(),
                e.getType(),
                e.getSource(),
                e.getCurrency(),
                e.getAmount(),
                new TransactionReference(e.getReference()),
                e.getCreatedAt()
        );
    }

    public static LedgerEntryEntity toEntity(LedgerEntry entry) {
        var e = new LedgerEntryEntity();
        e.setId(entry.id().value());
        e.setAccountId(entry.accountId());
        e.setType(entry.type());
        e.setSource(entry.source());
        e.setCurrency(entry.currency());
        e.setAmount(entry.amount());
        e.setReference(entry.reference().value());
        e.setCreatedAt(entry.createdAt());
        return e;
    }
}