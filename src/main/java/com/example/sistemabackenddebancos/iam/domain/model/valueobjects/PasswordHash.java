package com.example.sistemabackenddebancos.iam.domain.model.valueobjects;

import java.util.Objects;

public final class PasswordHash {

    private final String value;

    public PasswordHash(String value) {
        String v = Objects.requireNonNull(value, "PasswordHash cannot be null").trim();
        if (v.isEmpty()) {
            throw new IllegalArgumentException("PasswordHash cannot be empty");
        }
        this.value = v;
    }

    public String value() {
        return value;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasswordHash)) return false;
        PasswordHash that = (PasswordHash) o;
        return value.equals(that.value);
    }

    public int hashCode() {
        return value.hashCode();
    }

    public String toString() {
        return "****"; // no exponer hashes por logs
    }
}