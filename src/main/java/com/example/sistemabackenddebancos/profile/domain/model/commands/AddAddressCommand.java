package com.example.sistemabackenddebancos.profile.domain.model.commands;

import com.example.sistemabackenddebancos.profile.domain.model.enumerations.AddressType;
import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.ProfileId;

public record AddAddressCommand(
        ProfileId profileId,
        AddressType type,
        String line1,
        String line2,
        String city,
        String region,
        String country,
        String postalCode,
        boolean makePrimary
) {}