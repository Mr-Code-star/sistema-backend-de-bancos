package com.example.sistemabackenddebancos.profile.domain.model.valueobjects;

import com.example.sistemabackenddebancos.profile.domain.model.enumerations.DocumentType;

import java.util.Objects;

public record DocumentNumber(DocumentType type, String value) {

    public DocumentNumber {
        Objects.requireNonNull(type, "DocumentType cannot be null");
        Objects.requireNonNull(value, "DocumentNumber.value cannot be null");

        var s = value.trim();
        if (s.isEmpty()) throw new IllegalArgumentException("Document number cannot be empty");

        // Reglas mínimas (podemos endurecer luego)
        switch (type) {
            case DNI -> {
                if (!s.matches("^\\d{8}$")) throw new IllegalArgumentException("DNI must be 8 digits");
            }
            case CE -> {
                if (!s.matches("^[A-Za-z0-9]{8,12}$")) throw new IllegalArgumentException("CE must be 8-12 alphanumeric");
            }
            case PASSPORT -> {
                if (!s.matches("^[A-Za-z0-9]{6,12}$")) throw new IllegalArgumentException("Passport must be 6-12 alphanumeric");
            }
        }
        value = s;
    }
}