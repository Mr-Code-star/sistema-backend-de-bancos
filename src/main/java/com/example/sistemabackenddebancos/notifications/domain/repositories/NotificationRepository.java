package com.example.sistemabackenddebancos.notifications.domain.repositories;

import com.example.sistemabackenddebancos.notifications.domain.model.aggregates.Notification;
import com.example.sistemabackenddebancos.notifications.domain.model.valueobjects.*;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);

    Optional<Notification> findById(NotificationId id);

    List<Notification> findAllByRecipientId(RecipientId recipientId);
}