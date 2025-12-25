package com.example.sistemabackenddebancos.iam.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

public final class UserId {

    private final UUID value;

    public UserId(UUID value) {
        this.value = Objects.requireNonNull(value, "UserId cannot be null");
    }

    public static UserId newId() {
        return new UserId(UUID.randomUUID());
    }

    public UUID value() {
        return value;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId)) return false;
        UserId userId = (UserId) o;
        return value.equals(userId.value);
    }

    public int hashCode() {
        return value.hashCode();
    }

    public String toString() {
        return value.toString();
    }
}