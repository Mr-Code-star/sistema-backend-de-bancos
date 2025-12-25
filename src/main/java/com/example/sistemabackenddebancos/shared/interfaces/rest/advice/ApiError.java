package com.example.sistemabackenddebancos.shared.interfaces.rest.advice;

import java.time.Instant;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {}