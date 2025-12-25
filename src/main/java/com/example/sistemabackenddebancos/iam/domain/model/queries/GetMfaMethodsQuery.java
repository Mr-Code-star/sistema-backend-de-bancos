package com.example.sistemabackenddebancos.iam.domain.model.queries;

import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.UserId;

/** Consulta: listar métodos MFA del usuario */
public record GetMfaMethodsQuery(UserId userId) {}