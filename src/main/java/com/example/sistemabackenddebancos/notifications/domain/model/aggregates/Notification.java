package com.example.sistemabackenddebancos.notifications.domain.model.aggregates;

import com.example.sistemabackenddebancos.notifications.domain.model.enumerations.NotificationChannel;
import com.example.sistemabackenddebancos.notifications.domain.model.enumerations.NotificationStatus;
import com.example.sistemabackenddebancos.notifications.domain.model.enumerations.NotificationType;
import com.example.sistemabackenddebancos.notifications.domain.model.valueobjects.*;

import java.time.Instant;
import java.util.Objects;

public class Notification {

    private final NotificationId id;
    private final RecipientId recipientId;

    private final NotificationType type;
    private final NotificationChannel channel;

    private final Title title;
    private final Body body;

    private final NotificationStatus status;

    private final String reference; // correlación: transfer reference, accountId, etc. (opcional)
    private final Instant createdAt;

    public Notification(NotificationId id,
                        RecipientId recipientId,
                        NotificationType type,
                        NotificationChannel channel,
                        Title title,
                        Body body,
                        NotificationStatus status,
                        String reference,
                        Instant createdAt) {

        this.id = Objects.requireNonNull(id, "Notification.id cannot be null");
        this.recipientId = Objects.requireNonNull(recipientId, "recipientId cannot be null");
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.channel = Objects.requireNonNull(channel, "channel cannot be null");
        this.title = Objects.requireNonNull(title, "title cannot be null");
        this.body = Objects.requireNonNull(body, "body cannot be null");
        this.status = Objects.requireNonNull(status, "status cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");

        this.reference = (reference == null || reference.isBlank()) ? null : reference.trim();
    }

    public static Notification create(RecipientId recipientId,
                                      NotificationType type,
                                      NotificationChannel channel,
                                      Title title,
                                      Body body,
                                      String reference) {
        return new Notification(
                NotificationId.newId(),
                recipientId,
                type,
                channel,
                title,
                body,
                NotificationStatus.UNREAD,
                reference,
                Instant.now()
        );
    }

    public NotificationId id() { return id; }
    public RecipientId recipientId() { return recipientId; }
    public NotificationType type() { return type; }
    public NotificationChannel channel() { return channel; }
    public Title title() { return title; }
    public Body body() { return body; }
    public NotificationStatus status() { return status; }
    public String reference() { return reference; }
    public Instant createdAt() { return createdAt; }

    public Notification markRead() {
        if (status == NotificationStatus.READ) return this;
        if (status == NotificationStatus.ARCHIVED) return this;
        return new Notification(id, recipientId, type, channel, title, body, NotificationStatus.READ, reference, createdAt);
    }

    public Notification archive() {
        if (status == NotificationStatus.ARCHIVED) return this;
        return new Notification(id, recipientId, type, channel, title, body, NotificationStatus.ARCHIVED, reference, createdAt);
    }
}