package com.example.sistemabackenddebancos.accounts.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record AccountId(UUID value) {
    public AccountId {
        Objects.requireNonNull(value, "AccountId.value cannot be null");
    }
    public static AccountId newId() {
        return new AccountId(UUID.randomUUID());
    }
}
