package com.example.sistemabackenddebancos.notifications.domain.model.commands;

import com.example.sistemabackenddebancos.notifications.domain.model.valueobjects.NotificationId;

public record ArchiveNotificationCommand(NotificationId notificationId) {}
