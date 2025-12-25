package com.example.sistemabackenddebancos.iam.domain.model.readmodels;

import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.MfaType;

import java.util.UUID;

public record MfaMethodReadModel(
        UUID mfaMethodId,
        MfaType type,
        String destination,
        boolean verified
) {}