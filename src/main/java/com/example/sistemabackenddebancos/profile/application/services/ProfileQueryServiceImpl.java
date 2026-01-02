package com.example.sistemabackenddebancos.profile.application.services;

import com.example.sistemabackenddebancos.profile.domain.model.aggregates.Profile;
import com.example.sistemabackenddebancos.profile.domain.model.queries.GetNotificationPreferencesByUserIdQuery;
import com.example.sistemabackenddebancos.profile.domain.model.queries.GetProfileByIdQuery;
import com.example.sistemabackenddebancos.profile.domain.model.queries.GetProfileByUserIdQuery;
import com.example.sistemabackenddebancos.profile.domain.repositories.ProfileRepository;
import com.example.sistemabackenddebancos.profile.domain.services.ProfileQueryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileQueryServiceImpl implements ProfileQueryService {

    private final ProfileRepository profileRepository;

    public ProfileQueryServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> handle(GetProfileByIdQuery query) {
        return profileRepository.findById(query.profileId());
    }

    @Override
    public Optional<Profile> handle(GetProfileByUserIdQuery query) {
        return profileRepository.findByUserId(query.userId());
    }

    @Override
    public Optional<Profile> handle(GetNotificationPreferencesByUserIdQuery query) {
        return profileRepository.findByUserId(query.userId());
    }

}