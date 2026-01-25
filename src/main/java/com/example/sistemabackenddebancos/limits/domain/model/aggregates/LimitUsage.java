package com.example.sistemabackenddebancos.limits.domain.model.aggregates;

import com.example.sistemabackenddebancos.limits.domain.model.enumerations.LimitDecision;
import com.example.sistemabackenddebancos.limits.domain.model.enumerations.OperationType;
import com.example.sistemabackenddebancos.limits.domain.model.valueobjects.LimitUsageId;
import com.example.sistemabackenddebancos.limits.domain.model.valueobjects.PeriodKey;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class LimitUsage {

    private final LimitUsageId id;
    private final UUID userId;
    private final OperationType operationType;
    private final PeriodKey period;

    private final BigDecimal usedAmount;
    private final int usedCount;

    public LimitUsage(LimitUsageId id,
                      UUID userId,
                      OperationType operationType,
                      PeriodKey period,
                      BigDecimal usedAmount,
                      int usedCount) {

        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.userId = Objects.requireNonNull(userId, "userId cannot be null");
        this.operationType = Objects.requireNonNull(operationType, "operationType cannot be null");
        this.period = Objects.requireNonNull(period, "period cannot be null");

        this.usedAmount = Objects.requireNonNull(usedAmount, "usedAmount cannot be null");
        if (usedAmount.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("usedAmount cannot be negative");

        if (usedCount < 0) throw new IllegalArgumentException("usedCount cannot be negative");
        this.usedCount = usedCount;
    }

    public static LimitUsage newDay(UUID userId, OperationType op, PeriodKey period) {
        return new LimitUsage(LimitUsageId.newId(), userId, op, period, BigDecimal.ZERO, 0);
    }

    public LimitUsageId id() { return id; }
    public UUID userId() { return userId; }
    public OperationType operationType() { return operationType; }
    public PeriodKey period() { return period; }
    public BigDecimal usedAmount() { return usedAmount; }
    public int usedCount() { return usedCount; }

    public record CheckResult(LimitDecision decision, String reason, BigDecimal newUsedAmount, int newUsedCount) {}

    /**
     * Verifica si se puede consumir (amount +1 count) contra límites máximos.
     * Si ALLOW, retorna nuevos valores sugeridos.
     */
    public CheckResult check(BigDecimal amountToConsume, BigDecimal maxDailyAmount, int maxDailyCount) {
        Objects.requireNonNull(amountToConsume, "amountToConsume cannot be null");
        Objects.requireNonNull(maxDailyAmount, "maxDailyAmount cannot be null");

        if (amountToConsume.compareTo(BigDecimal.ZERO) <= 0) {
            return new CheckResult(LimitDecision.DENY, "Amount must be > 0", usedAmount, usedCount);
        }

        BigDecimal nextAmount = usedAmount.add(amountToConsume);
        int nextCount = usedCount + 1;

        if (nextCount > maxDailyCount) {
            return new CheckResult(LimitDecision.DENY, "Daily operation count limit exceeded", usedAmount, usedCount);
        }

        if (nextAmount.compareTo(maxDailyAmount) > 0) {
            return new CheckResult(LimitDecision.DENY, "Daily amount limit exceeded", usedAmount, usedCount);
        }

        return new CheckResult(LimitDecision.ALLOW, null, nextAmount, nextCount);
    }

    public LimitUsage consume(BigDecimal newUsedAmount, int newUsedCount) {
        return new LimitUsage(id, userId, operationType, period, newUsedAmount, newUsedCount);
    }
}