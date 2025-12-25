package com.example.sistemabackenddebancos.iam.domain.model.commands;

import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.Email;

/**
 * Caso de uso: Registrar usuario (UserRegistered)
 * - Crea identidad digital (email + password)
 * - No incluye CustomerProfile (eso es otro BC)
 */
public record RegisterUserCommand(
        Email email,
        String plainPassword
) {}