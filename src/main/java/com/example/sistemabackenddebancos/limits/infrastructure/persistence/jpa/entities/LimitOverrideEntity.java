package com.example.sistemabackenddebancos.limits.infrastructure.persistence.jpa.entities;

import com.example.sistemabackenddebancos.limits.domain.model.enumerations.OperationType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "limit_overrides",
        uniqueConstraints = @UniqueConstraint(name = "uk_override_user_op", columnNames = {"user_id", "operation_type"}))
public class LimitOverrideEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;

    @Column(name = "max_daily_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal maxDailyAmount;

    @Column(name = "max_daily_count", nullable = false)
    private int maxDailyCount;

    @Column(name = "reason")
    private String reason;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public LimitOverrideEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public OperationType getOperationType() { return operationType; }
    public void setOperationType(OperationType operationType) { this.operationType = operationType; }

    public BigDecimal getMaxDailyAmount() { return maxDailyAmount; }
    public void setMaxDailyAmount(BigDecimal maxDailyAmount) { this.maxDailyAmount = maxDailyAmount; }

    public int getMaxDailyCount() { return maxDailyCount; }
    public void setMaxDailyCount(int maxDailyCount) { this.maxDailyCount = maxDailyCount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}