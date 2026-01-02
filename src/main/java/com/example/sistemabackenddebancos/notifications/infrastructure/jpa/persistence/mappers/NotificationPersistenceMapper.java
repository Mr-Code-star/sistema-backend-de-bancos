package com.example.sistemabackenddebancos.notifications.infrastructure.jpa.persistence.mappers;

import com.example.sistemabackenddebancos.notifications.domain.model.aggregates.Notification;
import com.example.sistemabackenddebancos.notifications.domain.model.valueobjects.Body;
import com.example.sistemabackenddebancos.notifications.domain.model.valueobjects.NotificationId;
import com.example.sistemabackenddebancos.notifications.domain.model.valueobjects.RecipientId;
import com.example.sistemabackenddebancos.notifications.domain.model.valueobjects.Title;
import com.example.sistemabackenddebancos.notifications.infrastructure.jpa.persistence.entities.NotificationEntity;

public class NotificationPersistenceMapper {

    public static Notification toDomain(NotificationEntity e) {
        return new Notification(
                new NotificationId(e.getId()),
                new RecipientId(e.getRecipientId()),
                e.getType(),
                e.getChannel(),
                new Title(e.getTitle()),
                new Body(e.getBody()),
                e.getStatus(),
                e.getReference(),
                e.getCreatedAt()
        );
    }

    public static NotificationEntity toEntity(Notification n) {
        var e = new NotificationEntity();
        e.setId(n.id().value());
        e.setRecipientId(n.recipientId().value());
        e.setType(n.type());
        e.setChannel(n.channel());
        e.setTitle(n.title().value());
        e.setBody(n.body().value());
        e.setStatus(n.status());
        e.setReference(n.reference());
        e.setCreatedAt(n.createdAt());
        return e;
    }
}