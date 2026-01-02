package com.example.sistemabackenddebancos.profile.interfaces.rest.dtos.responses;

import java.util.List;
import java.util.Map;

public record NotificationPreferencesResponse(
        boolean emailEnabled,
        boolean smsEnabled,
        Map<String, List<String>> channelsByType
) {}