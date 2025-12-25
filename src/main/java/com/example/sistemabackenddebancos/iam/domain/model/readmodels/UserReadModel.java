package com.example.sistemabackenddebancos.iam.domain.model.readmodels;

import com.example.sistemabackenddebancos.iam.domain.model.enumerations.UserStatus;

import java.util.UUID;

public record UserReadModel(
        UUID userId,
        String email,
        UserStatus status,
        boolean mfaEnabled,
        int failedAttempts
) {}