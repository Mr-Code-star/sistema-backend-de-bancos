package com.example.sistemabackenddebancos.profile.domain.model.commands;

import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.AddressId;
import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.ProfileId;

public record SetPrimaryAddressCommand(
        ProfileId profileId,
        AddressId addressId
) {}