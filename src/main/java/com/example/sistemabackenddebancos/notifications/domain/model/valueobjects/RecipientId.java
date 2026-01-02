package com.example.sistemabackenddebancos.notifications.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record RecipientId(UUID value) {
    public RecipientId {
        Objects.requireNonNull(value, "RecipientId.value cannot be null");
    }
}