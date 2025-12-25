package com.example.sistemabackenddebancos.iam.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

public final class MfaMethodId {

    private final UUID value;

    public MfaMethodId(UUID value) {
        this.value = Objects.requireNonNull(value, "MfaMethodId cannot be null");
    }

    public static MfaMethodId newId() {
        return new MfaMethodId(UUID.randomUUID());
    }

    public UUID value() {
        return value;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MfaMethodId)) return false;
        MfaMethodId that = (MfaMethodId) o;
        return value.equals(that.value);
    }

    public int hashCode() {
        return value.hashCode();
    }

    public String toString() {
        return value.toString();
    }
}