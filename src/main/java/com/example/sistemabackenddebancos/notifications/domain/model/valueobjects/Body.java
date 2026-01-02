package com.example.sistemabackenddebancos.notifications.domain.model.valueobjects;

import java.util.Objects;

public record Body(String value) {
    public Body {
        Objects.requireNonNull(value, "Body cannot be null");
        var s = value.trim();
        if (s.isEmpty()) throw new IllegalArgumentException("Body cannot be empty");
        if (s.length() > 500) throw new IllegalArgumentException("Body too long (max 500)");
        value = s;
    }
}