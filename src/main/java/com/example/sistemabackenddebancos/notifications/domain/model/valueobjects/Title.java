package com.example.sistemabackenddebancos.notifications.domain.model.valueobjects;

import java.util.Objects;

public record Title(String value) {
    public Title {
        Objects.requireNonNull(value, "Title cannot be null");
        var s = value.trim();
        if (s.isEmpty()) throw new IllegalArgumentException("Title cannot be empty");
        if (s.length() > 80) throw new IllegalArgumentException("Title too long (max 80)");
        value = s;
    }
}