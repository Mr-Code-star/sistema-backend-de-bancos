package com.example.sistemabackenddebancos.profile.infrastructure.persistence.jpa.mappers;

import com.example.sistemabackenddebancos.notifications.domain.model.enumerations.NotificationChannel;
import com.example.sistemabackenddebancos.notifications.domain.model.enumerations.NotificationType;
import com.example.sistemabackenddebancos.profile.domain.model.entities.NotificationPreferences;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class NotificationPreferencesJsonMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // DTO interno (solo para persistencia JSON)
    public record PrefsPayload(
            boolean emailEnabled,
            boolean smsEnabled,
            Map<NotificationType, Set<NotificationChannel>> channelsByType
    ) {}

    public static String toJson(NotificationPreferences prefs) {
        try {
            var payload = new PrefsPayload(
                    prefs.emailEnabled(),
                    prefs.smsEnabled(),
                    prefs.channelsByType()
            );
            return MAPPER.writeValueAsString(payload);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize NotificationPreferences", ex);
        }
    }

    public static NotificationPreferences fromJson(String json) {
        if (json == null || json.isBlank()) {
            return NotificationPreferences.defaults();
        }

        try {
            var payload = MAPPER.readValue(json, PrefsPayload.class);

            // Normaliza map (si viene null)
            Map<NotificationType, Set<NotificationChannel>> map =
                    payload.channelsByType() == null
                            ? new EnumMap<>(NotificationType.class)
                            : new EnumMap<>(payload.channelsByType());

            // Defaults si falta algo
            for (var type : NotificationType.values()) {
                map.putIfAbsent(type, EnumSet.of(NotificationChannel.IN_APP));
            }

            return new NotificationPreferences(payload.emailEnabled(), payload.smsEnabled(), map);
        } catch (Exception ex) {
            // Si el JSON está corrupto, no rompas el sistema: vuelve a defaults
            return NotificationPreferences.defaults();
        }
    }
}