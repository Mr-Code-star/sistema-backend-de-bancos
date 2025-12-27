package com.example.sistemabackenddebancos.profile.interfaces.rest.dtos.requests;

public record AddAddressRequest(
        String type,      // HOME/WORK/BILLING
        String line1,
        String line2,
        String city,
        String region,
        String country,
        String postalCode,
        boolean makePrimary
) {}
