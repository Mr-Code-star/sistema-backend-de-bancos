package com.example.sistemabackenddebancos.limits.infrastructure.persistence.jpa.adapters;

import com.example.sistemabackenddebancos.limits.domain.model.aggregates.LimitUsage;
import com.example.sistemabackenddebancos.limits.domain.model.enumerations.OperationType;
import com.example.sistemabackenddebancos.limits.domain.model.valueobjects.PeriodKey;
import com.example.sistemabackenddebancos.limits.domain.repositories.LimitUsageRepository;
import com.example.sistemabackenddebancos.limits.infrastructure.persistence.jpa.mappers.LimitUsagePersistenceMapper;
import com.example.sistemabackenddebancos.limits.infrastructure.persistence.jpa.repositories.SpringDataLimitUsageJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaLimitUsageRepositoryAdapter implements LimitUsageRepository {

    private final SpringDataLimitUsageJpaRepository jpa;

    public JpaLimitUsageRepositoryAdapter(SpringDataLimitUsageJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<LimitUsage> findByUserIdAndOperationAndPeriod(UUID userId, OperationType operationType, PeriodKey period) {
        return jpa.findByUserIdAndOperationTypeAndDay(userId, operationType, period.day())
                .map(LimitUsagePersistenceMapper::toDomain);
    }

    @Override
    public LimitUsage save(LimitUsage usage) {
        var saved = jpa.save(LimitUsagePersistenceMapper.toEntity(usage));
        return LimitUsagePersistenceMapper.toDomain(saved);
    }
}