package com.example.sistemabackenddebancos.shared.domain.model.valueobjects;

import com.example.sistemabackenddebancos.shared.domain.model.enumerations.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) {

    public Money {
        Objects.requireNonNull(amount, "Money.amount cannot be null");
        Objects.requireNonNull(currency, "Money.currency cannot be null");

        amount = amount.setScale(2, RoundingMode.HALF_UP);
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money.amount cannot be negative");
        }
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        ensureSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        ensureSameCurrency(other);
        var result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        return new Money(result, this.currency);
    }

    private void ensureSameCurrency(Money other) {
        Objects.requireNonNull(other, "Money other cannot be null");
        if (this.currency != other.currency) {
            throw new IllegalArgumentException("Currency mismatch");
        }
    }
}