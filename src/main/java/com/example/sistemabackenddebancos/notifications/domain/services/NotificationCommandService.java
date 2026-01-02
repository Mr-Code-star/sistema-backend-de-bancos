package com.example.sistemabackenddebancos.notifications.domain.services;

import com.example.sistemabackenddebancos.notifications.domain.model.aggregates.Notification;
import com.example.sistemabackenddebancos.notifications.domain.model.commands.*;

import java.util.Optional;

public interface NotificationCommandService {
    Notification handle(CreateNotificationCommand command);
    Optional<Notification> handle(MarkAsReadCommand command);
    Optional<Notification> handle(ArchiveNotificationCommand command);
}