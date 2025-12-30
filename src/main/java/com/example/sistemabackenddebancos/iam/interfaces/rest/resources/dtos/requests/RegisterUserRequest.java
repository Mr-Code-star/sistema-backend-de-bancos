package com.example.sistemabackenddebancos.iam.interfaces.rest.resources.dtos.requests;

public record RegisterUserRequest(
        String email,
        String password
) {}