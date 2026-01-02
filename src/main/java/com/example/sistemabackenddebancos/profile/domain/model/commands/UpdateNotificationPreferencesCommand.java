package com.example.sistemabackenddebancos.profile.domain.model.commands;

import com.example.sistemabackenddebancos.profile.domain.model.entities.NotificationPreferences;
import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.ProfileId;

public record UpdateNotificationPreferencesCommand(
        ProfileId profileId,
        NotificationPreferences preferences
) {}