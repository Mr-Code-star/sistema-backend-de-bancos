package com.example.sistemabackenddebancos.ledger.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record EntryId(UUID value) {
    public EntryId {
        Objects.requireNonNull(value, "EntryId.value cannot be null");
    }

    public static EntryId newId() {
        return new EntryId(UUID.randomUUID());
    }
}