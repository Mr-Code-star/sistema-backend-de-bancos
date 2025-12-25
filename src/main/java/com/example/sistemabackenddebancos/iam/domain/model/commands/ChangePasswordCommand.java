package com.example.sistemabackenddebancos.iam.domain.model.commands;

import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.UserId;

/**
 * Caso de uso: Cambiar contraseña
 * - Requiere contraseña actual y nueva
 */
public record ChangePasswordCommand(
        UserId userId,
        String currentPlainPassword,
        String newPlainPassword
) {}