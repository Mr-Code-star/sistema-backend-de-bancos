package com.example.sistemabackenddebancos.iam.interfaces.rest.resources.dtos.requests;

public record LoginRequest(
        String email,
        String password,
        String mfaCode
) {}