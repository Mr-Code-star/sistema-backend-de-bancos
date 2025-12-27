package com.example.sistemabackenddebancos.profile.application.services;

import com.example.sistemabackenddebancos.profile.domain.model.aggregates.Profile;
import com.example.sistemabackenddebancos.profile.domain.model.commands.*;
import com.example.sistemabackenddebancos.profile.domain.repositories.ProfileRepository;
import com.example.sistemabackenddebancos.profile.domain.services.ProfileCommandService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final ProfileRepository profileRepository;

    public ProfileCommandServiceImpl(ProfileRepository profilerepository) {
        this.profileRepository = profilerepository;
    }

    @Override
    public Optional<Profile> handle(CreateProfileCommand command) {
        if (profileRepository.existsByUserId(command.userId())) {
            return Optional.empty();
        }

        var profile = Profile.createNew(
                command.userId(),
                command.fullName(),
                command.phoneNumber(),
                command.documentNumber()
        );

        return Optional.of(profileRepository.save(profile));
    }

    @Override
    public Optional<Profile> handle(UpdateProfileCommand command) {
        var profileOpt = profileRepository.findById(command.profileId());
        if (profileOpt.isEmpty()) return Optional.empty();

        var updated = profileOpt.get().updateBasicInfo(command.fullName(), command.phoneNumber());
        return Optional.of(profileRepository.save(updated));
    }

    @Override
    public Optional<Profile> handle(AddAddressCommand command) {
        var profileOpt = profileRepository.findById(command.profileId());
        if (profileOpt.isEmpty()) return Optional.empty();

        var updated = profileOpt.get().addAddress(
                command.type(),
                command.line1(),
                command.line2(),
                command.city(),
                command.region(),
                command.country(),
                command.postalCode(),
                command.makePrimary()
        );

        return Optional.of(profileRepository.save(updated));
    }

    @Override
    public Optional<Profile> handle(UpdateAddressCommand command) {
        var profileOpt = profileRepository.findById(command.profileId());
        if (profileOpt.isEmpty()) return Optional.empty();

        var updated = profileOpt.get().updateAddress(
                command.addressId(),
                command.type(),
                command.line1(),
                command.line2(),
                command.city(),
                command.region(),
                command.country(),
                command.postalCode()
        );

        return Optional.of(profileRepository.save(updated));
    }

    @Override
    public Optional<Profile> handle(RemoveAddressCommand command) {
        var profileOpt = profileRepository.findById(command.profileId());
        if (profileOpt.isEmpty()) return Optional.empty();

        var updated = profileOpt.get().removeAddress(command.addressId());
        return Optional.of(profileRepository.save(updated));
    }

    @Override
    public Optional<Profile> handle(SetPrimaryAddressCommand command) {
        var profileOpt = profileRepository.findById(command.profileId());
        if (profileOpt.isEmpty()) return Optional.empty();

        var updated = profileOpt.get().setPrimaryAddress(command.addressId());
        return Optional.of(profileRepository.save(updated));
    }
}