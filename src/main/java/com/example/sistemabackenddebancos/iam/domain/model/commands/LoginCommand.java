package com.example.sistemabackenddebancos.iam.domain.model.commands;

import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.Email;

/**
 * Caso de uso: Login (usuario se autentica)
 * - Si MFA está habilitado, puede requerir código.
 */
public record LoginCommand(
        Email email,
        String plainPassword,
        String mfaCode // nullable si no aplica
) {}