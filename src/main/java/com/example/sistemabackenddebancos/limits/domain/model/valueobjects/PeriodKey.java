package com.example.sistemabackenddebancos.limits.domain.model.valueobjects;

import java.time.LocalDate;
import java.util.Objects;

public record PeriodKey(LocalDate day) {
    public PeriodKey {
        Objects.requireNonNull(day, "day cannot be null");
    }

    public static PeriodKey todayUTC() {
        return new PeriodKey(LocalDate.now(java.time.Clock.systemUTC()));
    }
}