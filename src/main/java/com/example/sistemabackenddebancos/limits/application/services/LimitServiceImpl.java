package com.example.sistemabackenddebancos.limits.application.services;

import com.example.sistemabackenddebancos.limits.domain.model.aggregates.LimitUsage;
import com.example.sistemabackenddebancos.limits.domain.model.commands.CheckAndConsumeLimitCommand;
import com.example.sistemabackenddebancos.limits.domain.model.enumerations.OperationType;
import com.example.sistemabackenddebancos.limits.domain.model.valueobjects.PeriodKey;
import com.example.sistemabackenddebancos.limits.domain.repositories.LimitUsageRepository;
import com.example.sistemabackenddebancos.limits.domain.services.LimitService;
import com.example.sistemabackenddebancos.limits.infrastructure.persistence.jpa.repositories.SpringDataLimitOverrideJpaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

@Service
public class LimitServiceImpl implements LimitService {

    private final LimitUsageRepository limitUsageRepository;
    private final SpringDataLimitOverrideJpaRepository overrideRepo;

    // Límites diarios (MVP): maxAmount + maxCount
    private final Map<OperationType, BigDecimal> maxAmountDaily = new EnumMap<>(OperationType.class);
    private final Map<OperationType, Integer> maxCountDaily = new EnumMap<>(OperationType.class);

    public LimitServiceImpl(LimitUsageRepository limitUsageRepository, SpringDataLimitOverrideJpaRepository overrideRepo) {
        this.limitUsageRepository = limitUsageRepository;
        this.overrideRepo = overrideRepo;

        // ✅ Ajusta estos valores como quieras
        maxAmountDaily.put(OperationType.TRANSFER, new BigDecimal("5000.00"));
        maxCountDaily.put(OperationType.TRANSFER, 10);

        maxAmountDaily.put(OperationType.PAYMENT, new BigDecimal("6000.00"));
        maxCountDaily.put(OperationType.PAYMENT, 15);

        maxAmountDaily.put(OperationType.WITHDRAW, new BigDecimal("2000.00"));
        maxCountDaily.put(OperationType.WITHDRAW, 5);
    }

    @Override
    public LimitUsage.CheckResult checkAndConsume(CheckAndConsumeLimitCommand command) {
        var period = PeriodKey.todayUTC();

        var usage = limitUsageRepository.findByUserIdAndOperationAndPeriod(
                command.userId(),
                command.operationType(),
                period
        ).orElseGet(() -> LimitUsage.newDay(command.userId(), command.operationType(), period));

        BigDecimal maxAmount = maxAmountDaily.getOrDefault(command.operationType(), new BigDecimal("0.00"));
        int maxCount = maxCountDaily.getOrDefault(command.operationType(), 0);

        var overrideOpt = overrideRepo.findByUserIdAndOperationType(command.userId(), command.operationType());
        if (overrideOpt.isPresent()) {
            maxAmount = overrideOpt.get().getMaxDailyAmount();
            maxCount = overrideOpt.get().getMaxDailyCount();
        }
        var result = usage.check(command.amount(), maxAmount, maxCount);

        if (result.decision() == com.example.sistemabackenddebancos.limits.domain.model.enumerations.LimitDecision.ALLOW) {
            var updated = usage.consume(result.newUsedAmount(), result.newUsedCount());
            limitUsageRepository.save(updated);
        }

        return result;
    }
}
