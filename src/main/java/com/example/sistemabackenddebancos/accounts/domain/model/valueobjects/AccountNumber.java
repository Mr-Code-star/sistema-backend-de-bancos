package com.example.sistemabackenddebancos.accounts.domain.model.valueobjects;

import java.util.Objects;

public record AccountNumber(String value) {
    public AccountNumber {
        Objects.requireNonNull(value, "AccountNumber.value cannot be null");
        var s = value.trim();
        if (s.isEmpty()) throw new IllegalArgumentException("AccountNumber cannot be empty");

        // Simple y claro: solo dígitos 10-20 (tú defines)
        if (!s.matches("^\\d{10,20}$")) {
            throw new IllegalArgumentException("AccountNumber must be 10 to 20 digits");
        }

        value = s;
    }
}