package com.example.sistemabackenddebancos.profile.interfaces.rest.dtos.responses;

public record AddressResponse(
        String addressId,
        String type,
        String line1,
        String line2,
        String city,
        String region,
        String country,
        String postalCode,
        boolean primary
) {}
