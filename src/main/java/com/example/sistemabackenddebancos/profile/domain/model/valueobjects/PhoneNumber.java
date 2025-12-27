package com.example.sistemabackenddebancos.profile.domain.model.valueobjects;

import java.util.Objects;

public record PhoneNumber(String value) {

    public PhoneNumber {
        Objects.requireNonNull(value, "PhoneNumber.value cannot be null");
        var s = value.trim();
        if (s.isEmpty()) throw new IllegalArgumentException("PhoneNumber cannot be empty");

        // Validación simple (E.164-ish). Ajustamos luego si quieres Perú específico.
        if (!s.matches("^\\+?[0-9]{8,15}$")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        value = s;
    }
}