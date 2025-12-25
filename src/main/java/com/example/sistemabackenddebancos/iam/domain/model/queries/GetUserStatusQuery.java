package com.example.sistemabackenddebancos.iam.domain.model.queries;

import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.UserId;

/** Consulta: obtener estado del usuario (ACTIVE/PENDING/BLOCKED) */
public record GetUserStatusQuery(UserId userId) {}