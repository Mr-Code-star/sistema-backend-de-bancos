package com.example.sistemabackenddebancos.admin.application.services;

import com.example.sistemabackenddebancos.limits.domain.model.enumerations.OperationType;
import com.example.sistemabackenddebancos.limits.infrastructure.persistence.jpa.repositories.SpringDataLimitUsageJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class AdminLimitService {

    private final SpringDataLimitUsageJpaRepository limitRepo;

    public AdminLimitService(SpringDataLimitUsageJpaRepository limitRepo) {
        this.limitRepo = limitRepo;
    }

    @Transactional
    public long resetToday(UUID userId, OperationType operationTypeOrNull) {
        LocalDate todayUtc = LocalDate.now(Clock.systemUTC());

        if (operationTypeOrNull == null) {
            return limitRepo.deleteByUserIdAndDay(userId, todayUtc);
        }

        return limitRepo.deleteByUserIdAndOperationTypeAndDay(userId, operationTypeOrNull, todayUtc);
    }
}