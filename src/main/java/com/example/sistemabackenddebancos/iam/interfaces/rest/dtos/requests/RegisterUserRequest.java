package com.example.sistemabackenddebancos.iam.interfaces.rest.dtos.requests;

public record RegisterUserRequest(
        String email,
        String password
) {}