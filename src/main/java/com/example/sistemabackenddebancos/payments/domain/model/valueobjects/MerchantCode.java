package com.example.sistemabackenddebancos.payments.domain.model.valueobjects;

import java.util.Objects;

public record MerchantCode(String value) {
    public MerchantCode {
        Objects.requireNonNull(value, "MerchantCode cannot be null");
        var s = value.trim();
        if (s.isEmpty()) throw new IllegalArgumentException("MerchantCode cannot be empty");
        if (!s.matches("^[A-Z0-9_\\-]{3,40}$")) {
            throw new IllegalArgumentException("MerchantCode must be 3-40 chars (A-Z,0-9,_,-)");
        }
        value = s;
    }
}