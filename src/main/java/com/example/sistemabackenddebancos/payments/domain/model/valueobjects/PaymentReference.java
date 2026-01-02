package com.example.sistemabackenddebancos.payments.domain.model.valueobjects;

import java.util.Objects;

public record PaymentReference(String value) {
    public PaymentReference {
        Objects.requireNonNull(value, "PaymentReference cannot be null");
        var s = value.trim();
        if (s.isEmpty()) throw new IllegalArgumentException("PaymentReference cannot be empty");
        if (!s.matches("^[A-Za-z0-9\\-_]{8,80}$")) {
            throw new IllegalArgumentException("PaymentReference must be 8-80 chars (A-Z,0-9,-,_)");
        }
        value = s;
    }
}