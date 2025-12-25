package com.example.sistemabackenddebancos.iam.interfaces.rest.resources.dtos;

public record AuthResponse(
        String userId,
        String email,
        String status,
        String token
) {}