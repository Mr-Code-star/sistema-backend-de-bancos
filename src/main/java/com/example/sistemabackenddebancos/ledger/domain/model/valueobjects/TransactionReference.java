package com.example.sistemabackenddebancos.ledger.domain.model.valueobjects;

import java.util.Objects;

public record TransactionReference(String value) {
    public TransactionReference {
        Objects.requireNonNull(value, "TransactionReference cannot be null");
        var s = value.trim();
        if (s.isEmpty()) throw new IllegalArgumentException("TransactionReference cannot be empty");
        if (!s.matches("^[A-Za-z0-9\\-_]{8,80}$")) {
            throw new IllegalArgumentException("Invalid transaction reference format");
        }
        value = s;
    }
}
