package com.example.sistemabackenddebancos.shared.domain.model.valueobjects;

import com.example.sistemabackenddebancos.shared.domain.exceptions.BusinessRuleViolationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) {

    public Money {
        if (amount == null) throw new BusinessRuleViolationException("Amount is required");
        if (currency == null) throw new BusinessRuleViolationException("Currency is required");
        amount = amount.setScale(2, RoundingMode.HALF_UP);
        if (amount.compareTo(BigDecimal.ZERO) < 0) throw new BusinessRuleViolationException("Amount cannot be negative");
    }

    public Money add(Money other) {
        ensureSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        ensureSameCurrency(other);
        var result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) throw new BusinessRuleViolationException("Insufficient funds");
        return new Money(result, this.currency);
    }

    private void ensureSameCurrency(Money other) {
        Objects.requireNonNull(other, "Money is required");
        if (this.currency != other.currency)
            throw new BusinessRuleViolationException("Currency mismatch");
    }
}