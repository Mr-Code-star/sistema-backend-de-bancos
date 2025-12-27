package com.example.sistemabackenddebancos.profile.domain.model.commands;

import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.*;

public record CreateProfileCommand(
        UserId userId,
        FullName fullName,
        PhoneNumber phoneNumber,
        DocumentNumber documentNumber // nullable
) {}