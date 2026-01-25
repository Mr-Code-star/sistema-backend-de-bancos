package com.example.sistemabackenddebancos.limits.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record LimitUsageId(UUID value) {
    public LimitUsageId {
        Objects.requireNonNull(value, "LimitUsageId.value cannot be null");
    }

    public static LimitUsageId newId() {
        return new LimitUsageId(UUID.randomUUID());
    }
}