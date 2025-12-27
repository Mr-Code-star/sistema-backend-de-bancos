package com.example.sistemabackenddebancos.profile.interfaces.rest.dtos.requests;

public record UpdateAddressRequest(
        String type,
        String line1,
        String line2,
        String city,
        String region,
        String country,
        String postalCode
) {}
