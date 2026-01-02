package com.example.sistemabackenddebancos.notifications.domain.model.commands;

import com.example.sistemabackenddebancos.notifications.domain.model.enumerations.*;
import com.example.sistemabackenddebancos.notifications.domain.model.valueobjects.*;

public record CreateNotificationCommand(
        RecipientId recipientId,
        NotificationType type,
        NotificationChannel channel,
        Title title,
        Body body,
        String reference // optional
) {}