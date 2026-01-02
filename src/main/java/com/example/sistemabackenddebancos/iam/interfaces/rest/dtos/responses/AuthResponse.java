package com.example.sistemabackenddebancos.iam.interfaces.rest.dtos.responses;

public record AuthResponse(
        String userId,
        String email,
        String status,
        String token
) {}