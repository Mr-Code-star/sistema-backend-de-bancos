package com.example.sistemabackenddebancos.limits.domain.model.commands;

import com.example.sistemabackenddebancos.limits.domain.model.enumerations.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

public record CheckAndConsumeLimitCommand(
        UUID userId,
        OperationType operationType,
        BigDecimal amount
) {}