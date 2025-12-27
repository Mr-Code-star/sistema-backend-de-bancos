package com.example.sistemabackenddebancos.profile.interfaces.rest.dtos.requests;

public record UpdateProfileRequest(
        String givenNames,
        String paternalSurname,
        String maternalSurname,
        String phoneNumber
) {}
