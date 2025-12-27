package com.example.sistemabackenddebancos.profile.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record AddressId(UUID value) {
    public AddressId {
        Objects.requireNonNull(value, "AddressId.value cannot be null");
    }

    public static AddressId newId() {
        return new AddressId(UUID.randomUUID());
    }
}