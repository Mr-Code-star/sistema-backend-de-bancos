package com.example.sistemabackenddebancos.shared.infrastructure.time;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SystemClockProvider implements ClockProvider {
    @Override public Instant now() { return Instant.now(); }
}