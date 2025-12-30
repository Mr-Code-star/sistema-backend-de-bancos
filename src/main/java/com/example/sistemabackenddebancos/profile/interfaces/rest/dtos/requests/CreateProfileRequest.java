package com.example.sistemabackenddebancos.profile.interfaces.rest.dtos.requests;

public record CreateProfileRequest(
        String givenNames,
        String paternalSurname,
        String maternalSurname,
        String phoneNumber,
        String documentType,   // DNI/CE/PASSPORT (opcional)
        String documentNumber  // opcional
) {}
