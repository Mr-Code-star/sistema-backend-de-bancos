package com.example.sistemabackenddebancos.shared.domain.model.events;

import java.time.Instant;

public interface DomainEvent {
    Instant occurredOn();
    String type();
}