package com.example.sistemabackenddebancos.iam.interfaces.rest.resources.dtos;

public record LoginRequest(
        String email,
        String password,
        String mfaCode
) {}