package com.example.sistemabackenddebancos.profile.domain.model.commands;

import com.example.sistemabackenddebancos.profile.domain.model.enumerations.AddressType;
import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.AddressId;
import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.ProfileId;

public record UpdateAddressCommand(
        ProfileId profileId,
        AddressId addressId,
        AddressType type,
        String line1,
        String line2,
        String city,
        String region,
        String country,
        String postalCode
) {}