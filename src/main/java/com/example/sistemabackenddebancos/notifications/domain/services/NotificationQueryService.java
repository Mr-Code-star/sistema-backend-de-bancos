package com.example.sistemabackenddebancos.notifications.domain.services;

import com.example.sistemabackenddebancos.notifications.domain.model.aggregates.Notification;
import com.example.sistemabackenddebancos.notifications.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

public interface NotificationQueryService {
    Optional<Notification> handle(GetNotificationByIdQuery query);
    List<Notification> handle(GetMyNotificationsQuery query);
}