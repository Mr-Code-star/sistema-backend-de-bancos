package com.example.sistemabackenddebancos.iam.domain.model.queries;


import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.Email;

/** Consulta: obtener usuario por email (útil para login / admin) */
public record GetUserByEmailQuery(Email email) {}