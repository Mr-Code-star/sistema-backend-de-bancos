package com.example.sistemabackenddebancos.limits.infrastructure.persistence.jpa.mappers;

import com.example.sistemabackenddebancos.limits.domain.model.aggregates.LimitUsage;
import com.example.sistemabackenddebancos.limits.domain.model.valueobjects.LimitUsageId;
import com.example.sistemabackenddebancos.limits.domain.model.valueobjects.PeriodKey;
import com.example.sistemabackenddebancos.limits.infrastructure.persistence.jpa.entities.LimitUsageEntity;

public class LimitUsagePersistenceMapper {

    public static LimitUsage toDomain(LimitUsageEntity e) {
        return new LimitUsage(
                new LimitUsageId(e.getId()),
                e.getUserId(),
                e.getOperationType(),
                new PeriodKey(e.getDay()),
                e.getUsedAmount(),
                e.getUsedCount()
        );
    }

    public static LimitUsageEntity toEntity(LimitUsage u) {
        var e = new LimitUsageEntity();
        e.setId(u.id().value());
        e.setUserId(u.userId());
        e.setOperationType(u.operationType());
        e.setDay(u.period().day());
        e.setUsedAmount(u.usedAmount());
        e.setUsedCount(u.usedCount());
        return e;
    }
}