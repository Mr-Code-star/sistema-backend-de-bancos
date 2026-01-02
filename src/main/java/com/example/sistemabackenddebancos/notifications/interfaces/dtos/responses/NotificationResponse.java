package com.example.sistemabackenddebancos.notifications.interfaces.dtos.responses;

public record NotificationResponse(
        String notificationId,
        String recipientId,
        String type,
        String channel,
        String title,
        String body,
        String status,
        String reference,
        String createdAt
) {}