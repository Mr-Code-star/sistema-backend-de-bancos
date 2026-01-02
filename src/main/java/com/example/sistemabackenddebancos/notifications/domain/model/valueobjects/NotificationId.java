package com.example.sistemabackenddebancos.notifications.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record NotificationId(UUID value) {
    public NotificationId {
        Objects.requireNonNull(value, "NotificationId.value cannot be null");
    }

    public static NotificationId newId() {
        return new NotificationId(UUID.randomUUID());
    }
}