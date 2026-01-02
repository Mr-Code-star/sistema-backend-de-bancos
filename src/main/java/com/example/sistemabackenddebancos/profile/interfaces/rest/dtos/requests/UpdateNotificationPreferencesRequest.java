package com.example.sistemabackenddebancos.profile.interfaces.rest.dtos.requests;

import java.util.List;
import java.util.Map;

public record UpdateNotificationPreferencesRequest(
        boolean emailEnabled,
        boolean smsEnabled,
        Map<String, List<String>> channelsByType
        // Ej:
        // {
        //   "SECURITY": ["IN_APP","EMAIL","SMS"],
        //   "TRANSFER": ["IN_APP","EMAIL"]
        // }
) {}