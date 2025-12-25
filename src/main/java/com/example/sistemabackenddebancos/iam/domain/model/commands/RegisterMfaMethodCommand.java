package com.example.sistemabackenddebancos.iam.domain.model.commands;

import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.MfaType;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.UserId;

/**
 * Caso de uso: Registrar método MFA
 * - Ej: EMAIL -> destino = correo
 * - Ej: SMS -> destino = teléfono
 */
public record RegisterMfaMethodCommand(
        UserId userId,
        MfaType type,
        String destination
) {}