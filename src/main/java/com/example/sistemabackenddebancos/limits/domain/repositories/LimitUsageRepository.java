package com.example.sistemabackenddebancos.limits.domain.repositories;

import com.example.sistemabackenddebancos.limits.domain.model.aggregates.LimitUsage;
import com.example.sistemabackenddebancos.limits.domain.model.enumerations.OperationType;
import com.example.sistemabackenddebancos.limits.domain.model.valueobjects.PeriodKey;

import java.util.Optional;
import java.util.UUID;

public interface LimitUsageRepository {
    Optional<LimitUsage> findByUserIdAndOperationAndPeriod(UUID userId, OperationType operationType, PeriodKey period);
    LimitUsage save(LimitUsage usage);
}