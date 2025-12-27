package com.example.sistemabackenddebancos.profile.domain.repositories;

import com.example.sistemabackenddebancos.profile.domain.model.aggregates.Profile;
import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.ProfileId;
import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.UserId;

import java.util.Optional;

public interface ProfileRepository {
    Optional<Profile> findById(ProfileId id);
    Optional<Profile> findByUserId(UserId userId);
    boolean existsByUserId(UserId userId);
    Profile save(Profile profile);
}