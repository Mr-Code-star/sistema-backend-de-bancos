package com.example.sistemabackenddebancos.iam.interfaces.rest.resources.dtos;

public record RegisterUserRequest(
        String email,
        String password
) {}