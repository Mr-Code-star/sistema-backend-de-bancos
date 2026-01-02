package com.example.sistemabackenddebancos.profile.domain.model.entities;

import com.example.sistemabackenddebancos.notifications.domain.model.enumerations.NotificationChannel;
import com.example.sistemabackenddebancos.notifications.domain.model.enumerations.NotificationType;

import java.util.*;

public class NotificationPreferences {

    private final boolean emailEnabled;
    private final boolean smsEnabled;

    // Para cada tipo: canales permitidos
    private final Map<NotificationType, Set<NotificationChannel>> channelsByType;

    public NotificationPreferences(boolean emailEnabled,
                                   boolean smsEnabled,
                                   Map<NotificationType, Set<NotificationChannel>> channelsByType) {

        this.emailEnabled = emailEnabled;
        this.smsEnabled = smsEnabled;

        Objects.requireNonNull(channelsByType, "channelsByType cannot be null");

        // Copia defensiva + normalización
        var copy = new EnumMap<NotificationType, Set<NotificationChannel>>(NotificationType.class);
        for (var type : NotificationType.values()) {
            var channels = channelsByType.getOrDefault(type, EnumSet.of(NotificationChannel.IN_APP));
            copy.put(type, EnumSet.copyOf(channels));
        }

        // Reglas “banco real” mínimas:
        // SECURITY siempre debe tener al menos IN_APP (y normalmente EMAIL)
        if (!copy.get(NotificationType.SECURITY).contains(NotificationChannel.IN_APP)) {
            throw new IllegalArgumentException("SECURITY notifications must include IN_APP");
        }

        // Si email está deshabilitado, no permitas EMAIL en ninguna preferencia
        if (!emailEnabled) {
            copy.values().forEach(set -> set.remove(NotificationChannel.EMAIL));
        }
        // Si sms está deshabilitado, no permitas SMS
        if (!smsEnabled) {
            copy.values().forEach(set -> set.remove(NotificationChannel.SMS));
        }

        this.channelsByType = copy;
    }

    public static NotificationPreferences defaults() {
        var map = new EnumMap<NotificationType, Set<NotificationChannel>>(NotificationType.class);

        map.put(NotificationType.SECURITY, EnumSet.of(NotificationChannel.IN_APP, NotificationChannel.EMAIL));
        map.put(NotificationType.TRANSFER, EnumSet.of(NotificationChannel.IN_APP, NotificationChannel.EMAIL));
        map.put(NotificationType.ACCOUNT, EnumSet.of(NotificationChannel.IN_APP));
        map.put(NotificationType.SYSTEM, EnumSet.of(NotificationChannel.IN_APP));

        return new NotificationPreferences(true, false, map); // email ON, sms OFF por defecto
    }

    public boolean emailEnabled() { return emailEnabled; }
    public boolean smsEnabled() { return smsEnabled; }

    public Set<NotificationChannel> channelsFor(NotificationType type) {
        return channelsByType.getOrDefault(type, EnumSet.of(NotificationChannel.IN_APP));
    }

    public Map<NotificationType, Set<NotificationChannel>> channelsByType() {
        // copia defensiva
        var out = new EnumMap<NotificationType, Set<NotificationChannel>>(NotificationType.class);
        for (var e : channelsByType.entrySet()) {
            out.put(e.getKey(), EnumSet.copyOf(e.getValue()));
        }
        return out;
    }
}