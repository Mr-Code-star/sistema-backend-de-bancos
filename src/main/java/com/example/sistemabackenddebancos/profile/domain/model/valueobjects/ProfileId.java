package com.example.sistemabackenddebancos.profile.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record ProfileId(UUID value) {
    public ProfileId {
        Objects.requireNonNull(value, "ProfileId.value cannot be null");
    }

    public static ProfileId newId() {
        return new ProfileId(UUID.randomUUID());
    }
}