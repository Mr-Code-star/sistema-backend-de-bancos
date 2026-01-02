package com.example.sistemabackenddebancos.iam.interfaces.rest.dtos.requests;

public record LoginRequest(
        String email,
        String password,
        String mfaCode
) {}