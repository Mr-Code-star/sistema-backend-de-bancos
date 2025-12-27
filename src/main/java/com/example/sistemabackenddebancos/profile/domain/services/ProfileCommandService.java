package com.example.sistemabackenddebancos.profile.domain.services;

import com.example.sistemabackenddebancos.profile.domain.model.aggregates.Profile;
import com.example.sistemabackenddebancos.profile.domain.model.commands.*;

import java.util.Optional;

public interface ProfileCommandService {
    Optional<Profile> handle(CreateProfileCommand command);
    Optional<Profile> handle(UpdateProfileCommand command);
    Optional<Profile> handle(AddAddressCommand command);
    Optional<Profile> handle(UpdateAddressCommand command);
    Optional<Profile> handle(RemoveAddressCommand command);
    Optional<Profile> handle(SetPrimaryAddressCommand command);
}