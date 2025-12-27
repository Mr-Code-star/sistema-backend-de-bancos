package com.example.sistemabackenddebancos.profile.domain.model.commands;

import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.FullName;
import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.PhoneNumber;
import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.ProfileId;

public record UpdateProfileCommand(
        ProfileId profileId,
        FullName fullName,
        PhoneNumber phoneNumber
) {}