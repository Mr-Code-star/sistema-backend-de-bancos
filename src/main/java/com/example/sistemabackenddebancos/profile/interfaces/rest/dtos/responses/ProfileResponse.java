package com.example.sistemabackenddebancos.profile.interfaces.rest.dtos.responses;

import java.util.List;

public record ProfileResponse(
        String profileId,
        String userId,
        String givenNames,
        String paternalSurname,
        String maternalSurname,
        String phoneNumber,
        String documentType,
        String documentNumber,
        String kycStatus,
        List<AddressResponse> addresses
) {}
