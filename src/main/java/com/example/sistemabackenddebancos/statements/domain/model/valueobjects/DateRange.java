package com.example.sistemabackenddebancos.statements.domain.model.valueobjects;

import java.time.LocalDate;
import java.util.Objects;

public record DateRange(LocalDate from, LocalDate to) {
    public DateRange {
        Objects.requireNonNull(from, "from cannot be null");
        Objects.requireNonNull(to, "to cannot be null");
        if (to.isBefore(from))
            throw new IllegalArgumentException("to date cannot be before from date");
    }
}
