package com.example.sistemabackenddebancos.payments.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

public record PaymentId(UUID value) {
    public PaymentId {
        Objects.requireNonNull(value, "PaymentId.value cannot be null");
    }

    public static PaymentId newId() {
        return new PaymentId(UUID.randomUUID());
    }
}