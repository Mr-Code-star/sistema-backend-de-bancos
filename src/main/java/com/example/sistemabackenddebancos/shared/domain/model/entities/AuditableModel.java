package com.example.sistemabackenddebancos.shared.domain.model.entities;

import java.time.Instant;

public abstract class AuditableModel {
    private Instant createdAt;
    private Instant updatedAt;

    protected AuditableModel() {}

    protected AuditableModel(Instant createdAt, Instant updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void markCreated(Instant now) {
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void markUpdated(Instant now) {
        this.updatedAt = now;
    }
}