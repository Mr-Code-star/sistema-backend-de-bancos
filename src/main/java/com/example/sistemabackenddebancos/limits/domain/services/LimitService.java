package com.example.sistemabackenddebancos.limits.domain.services;

import com.example.sistemabackenddebancos.limits.domain.model.aggregates.LimitUsage;
import com.example.sistemabackenddebancos.limits.domain.model.commands.CheckAndConsumeLimitCommand;

public interface LimitService {
    LimitUsage.CheckResult checkAndConsume(CheckAndConsumeLimitCommand command);
}