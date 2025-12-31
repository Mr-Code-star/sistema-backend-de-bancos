package com.example.sistemabackenddebancos.transfers.model.valueobjects;

import java.util.Objects;

public record TransferReference(String value) {
    public TransferReference {
        Objects.requireNonNull(value, "TransferReference.value cannot be null");
        var s = value.trim();
        if (s.isEmpty()) throw new IllegalArgumentException("TransferReference cannot be empty");

        // Puedes endurecerlo luego (UUID, ULID, etc.). Por ahora: 8-80 chars, alfanum + -_
        if (!s.matches("^[A-Za-z0-9\\-_]{8,80}$")) {
            throw new IllegalArgumentException("TransferReference must be 8-80 chars (A-Z,0-9,-,_)");
        }
        value = s;
    }
}