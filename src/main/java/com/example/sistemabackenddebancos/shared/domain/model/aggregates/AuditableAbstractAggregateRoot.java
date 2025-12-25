package com.example.sistemabackenddebancos.shared.domain.model.aggregates;

import com.example.sistemabackenddebancos.shared.domain.model.entities.AuditableModel;
import com.example.sistemabackenddebancos.shared.domain.model.events.DomainEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AuditableAbstractAggregateRoot extends AuditableModel {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected AuditableAbstractAggregateRoot() { super(); }

    protected AuditableAbstractAggregateRoot(Instant createdAt, Instant updatedAt) {
        super(createdAt, updatedAt);
    }

    protected void addDomainEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    public List<DomainEvent> pullDomainEvents() {
        var copy = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public List<DomainEvent> peekDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
}