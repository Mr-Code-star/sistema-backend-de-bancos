package com.example.sistemabackenddebancos.shared.infrastructure.time;

import java.time.Instant;

public interface ClockProvider {
    Instant now();
}