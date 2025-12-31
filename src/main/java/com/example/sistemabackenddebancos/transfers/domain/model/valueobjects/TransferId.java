package com.example.sistemabackenddebancos.transfers.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record TransferId(UUID value) {
    public TransferId {
        Objects.requireNonNull(value, "TransferId.value cannot be null");
    }

    public static TransferId newId() {
        return new TransferId(UUID.randomUUID());
    }
}