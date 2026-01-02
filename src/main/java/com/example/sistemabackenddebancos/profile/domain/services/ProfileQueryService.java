package com.example.sistemabackenddebancos.profile.domain.services;

import com.example.sistemabackenddebancos.profile.domain.model.aggregates.Profile;
import com.example.sistemabackenddebancos.profile.domain.model.queries.*;

import java.util.Optional;

public interface ProfileQueryService {
    Optional<Profile> handle(GetProfileByIdQuery query);
    Optional<Profile> handle(GetProfileByUserIdQuery query);

    Optional<Profile> handle(GetNotificationPreferencesByUserIdQuery query);
}