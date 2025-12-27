package com.example.sistemabackenddebancos.profile.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record UserId(UUID value) {
    public UserId {
        Objects.requireNonNull(value, "UserId.value cannot be null");
    }
}