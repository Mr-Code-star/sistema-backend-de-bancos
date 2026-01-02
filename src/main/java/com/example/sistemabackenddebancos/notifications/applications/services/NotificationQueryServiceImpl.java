package com.example.sistemabackenddebancos.notifications.applications.services;

import com.example.sistemabackenddebancos.notifications.domain.model.aggregates.Notification;
import com.example.sistemabackenddebancos.notifications.domain.model.queries.GetMyNotificationsQuery;
import com.example.sistemabackenddebancos.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.example.sistemabackenddebancos.notifications.domain.repositories.NotificationRepository;
import com.example.sistemabackenddebancos.notifications.domain.services.NotificationQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private final NotificationRepository notificationRepository;

    public NotificationQueryServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Optional<Notification> handle(GetNotificationByIdQuery query) {
        return notificationRepository.findById(query.notificationId());
    }

    @Override
    public List<Notification> handle(GetMyNotificationsQuery query) {
        return notificationRepository.findAllByRecipientId(query.recipientId());
    }
}