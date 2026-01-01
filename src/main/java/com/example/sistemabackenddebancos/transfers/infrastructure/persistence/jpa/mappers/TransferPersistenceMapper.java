package com.example.sistemabackenddebancos.transfers.infrastructure.persistence.jpa.mappers;

import com.example.sistemabackenddebancos.transfers.domain.model.aggregates.Transfer;
import com.example.sistemabackenddebancos.transfers.domain.model.valueobjects.TransferId;
import com.example.sistemabackenddebancos.transfers.domain.model.valueobjects.TransferReference;
import com.example.sistemabackenddebancos.transfers.infrastructure.persistence.jpa.entities.TransferEntity;

import java.time.Instant;

public class TransferPersistenceMapper {

    public static Transfer toDomain(TransferEntity e) {
        return new Transfer(
                new TransferId(e.getId()),
                new TransferReference(e.getReference()),
                e.getFromAccountId(),
                e.getToAccountId(),
                e.getCurrency(),
                e.getAmount(),
                e.getStatus(),
                e.getFailureReason()
        );
    }

    public static TransferEntity toEntity(Transfer t) {
        var e = new TransferEntity();
        e.setId(t.id().value());
        e.setReference(t.reference().value());
        e.setFromAccountId(t.fromAccountId());
        e.setToAccountId(t.toAccountId());
        e.setCurrency(t.currency());
        e.setAmount(t.amount());
        e.setStatus(t.status());
        e.setFailureReason(t.failureReason());

        var now = Instant.now();
        e.setCreatedAt(now);
        e.setUpdatedAt(now);

        return e;
    }

    // Para updates: mantener createdAt
    public static void applyToEntity(TransferEntity e, Transfer t) {
        e.setReference(t.reference().value());
        e.setFromAccountId(t.fromAccountId());
        e.setToAccountId(t.toAccountId());
        e.setCurrency(t.currency());
        e.setAmount(t.amount());
        e.setStatus(t.status());
        e.setFailureReason(t.failureReason());
        e.setUpdatedAt(Instant.now());
    }
}