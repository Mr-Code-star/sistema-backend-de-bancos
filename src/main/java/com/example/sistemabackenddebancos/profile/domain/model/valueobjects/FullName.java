package com.example.sistemabackenddebancos.profile.domain.model.valueobjects;

import java.util.Objects;

public record FullName(String givenNames, String paternalSurname, String maternalSurname) {

    public FullName {
        givenNames = normalizeRequired(givenNames, "givenNames");
        paternalSurname = normalizeRequired(paternalSurname, "paternalSurname");
        maternalSurname = normalizeRequired(maternalSurname, "maternalSurname");
    }

    public String display() {
        return givenNames + " " + paternalSurname + " " + maternalSurname;
    }

    private static String normalizeRequired(String v, String field) {
        Objects.requireNonNull(v, field + " cannot be null");
        var s = v.trim();
        if (s.isEmpty()) throw new IllegalArgumentException(field + " cannot be empty");
        return s;
    }
}