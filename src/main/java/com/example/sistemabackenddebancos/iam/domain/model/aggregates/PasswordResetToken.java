package com.example.sistemabackenddebancos.iam.domain.model.aggregates;

import com.example.sistemabackenddebancos.iam.domain.model.enumerations.PasswordResetTokenStatus;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.UserId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class PasswordResetToken {

    private final UUID id;
    private final UserId userId;
    private final String tokenHash; // SHA-256 hex
    private final Instant expiresAt;
    private final PasswordResetTokenStatus status;
    private final Instant usedAt;

    public PasswordResetToken(UUID id,
                              UserId userId,
                              String tokenHash,
                              Instant expiresAt,
                              PasswordResetTokenStatus status,
                              Instant usedAt) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.userId = Objects.requireNonNull(userId, "userId cannot be null");
        this.tokenHash = Objects.requireNonNull(tokenHash, "tokenHash cannot be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt cannot be null");
        this.status = Objects.requireNonNull(status, "status cannot be null");
        this.usedAt = usedAt;
    }

    public UUID id() { return id; }
    public UserId userId() { return userId; }
    public String tokenHash() { return tokenHash; }
    public Instant expiresAt() { return expiresAt; }
    public PasswordResetTokenStatus status() { return status; }
    public Instant usedAt() { return usedAt; }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    public PasswordResetToken markUsed(Instant now) {
        return new PasswordResetToken(id, userId, tokenHash, expiresAt, PasswordResetTokenStatus.USED, now);
    }

    public PasswordResetToken markExpired() {
        return new PasswordResetToken(id, userId, tokenHash, expiresAt, PasswordResetTokenStatus.EXPIRED, usedAt);
    }
}