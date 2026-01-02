package com.example.sistemabackenddebancos.profile.application.services;

import com.example.sistemabackenddebancos.iam.domain.model.queries.GetUserByIdQuery;
import com.example.sistemabackenddebancos.iam.domain.services.UserQueryService;
import com.example.sistemabackenddebancos.notifications.domain.model.commands.CreateNotificationCommand;
import com.example.sistemabackenddebancos.notifications.domain.model.enumerations.NotificationChannel;
import com.example.sistemabackenddebancos.notifications.domain.model.enumerations.NotificationType;
import com.example.sistemabackenddebancos.notifications.domain.model.valueobjects.Body;
import com.example.sistemabackenddebancos.notifications.domain.model.valueobjects.RecipientId;
import com.example.sistemabackenddebancos.notifications.domain.model.valueobjects.Title;
import com.example.sistemabackenddebancos.notifications.domain.services.NotificationCommandService;
import com.example.sistemabackenddebancos.notifications.interfaces.dispatch.email.EmailDispatcher;
import com.example.sistemabackenddebancos.notifications.interfaces.dispatch.sms.SmsDispatcher;
import com.example.sistemabackenddebancos.profile.domain.model.entities.NotificationPreferences;
import com.example.sistemabackenddebancos.profile.domain.model.queries.GetNotificationPreferencesByUserIdQuery;
import com.example.sistemabackenddebancos.profile.domain.model.queries.GetProfileByUserIdQuery;
import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.UserId;
import com.example.sistemabackenddebancos.profile.domain.services.ProfileQueryService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationOrchestrator {

    private final NotificationCommandService notificationCommandService;
    private final ProfileQueryService profileQueryService;
    private final UserQueryService userQueryService; // para obtener email
    private final EmailDispatcher emailDispatcher;
    private final SmsDispatcher smsDispatcher;

    public NotificationOrchestrator(NotificationCommandService notificationCommandService,
                                    ProfileQueryService profileQueryService,
                                    UserQueryService userQueryService,
                                    EmailDispatcher emailDispatcher,
                                    SmsDispatcher smsDispatcher) {
        this.notificationCommandService = notificationCommandService;
        this.profileQueryService = profileQueryService;
        this.userQueryService = userQueryService;
        this.emailDispatcher = emailDispatcher;
        this.smsDispatcher = smsDispatcher;
    }

    public void notifyTransfer(UUID recipientUserId, String title, String body, String reference) {

        // 1) Leer preferencias (si no hay profile -> defaults)
        var profileOpt = profileQueryService.handle(new GetNotificationPreferencesByUserIdQuery(new UserId(recipientUserId)));
        var prefs = profileOpt.isPresent()
                ? profileOpt.get().notificationPreferences()
                : NotificationPreferences.defaults();

        var channels = prefs.channelsFor(
                NotificationType.TRANSFER
        );

        // 2) IN_APP (guardar en BD)
        if (channels.contains(NotificationChannel.IN_APP)) {
            notificationCommandService.handle(new CreateNotificationCommand(
                    new RecipientId(recipientUserId),
                    NotificationType.TRANSFER,
                    NotificationChannel.IN_APP,
                    new Title(title),
                    new Body(body),
                    reference
            ));
        }

        // 3) EMAIL
        if (channels.contains(NotificationChannel.EMAIL)) {
            // email vive en IAM
            var userOpt = userQueryService.handle(new GetUserByIdQuery(new com.example.sistemabackenddebancos.iam.domain.model.valueobjects.UserId(recipientUserId)));
            userOpt.ifPresent(u -> emailDispatcher.send(u.email().value(), title, body));
        }

        // 4) SMS
        if (channels.contains(NotificationChannel.SMS)) {
            // phone vive en Profile (si lo tienes)
            var pOpt = profileQueryService.handle(new GetProfileByUserIdQuery(new UserId(recipientUserId)));
            pOpt.ifPresent(p -> smsDispatcher.send(p.phoneNumber().value(), body));
        }
    }

    public void notifyAccount(UUID recipientUserId, String title, String body, String reference) {

        var profileOpt = profileQueryService.handle(
                new GetNotificationPreferencesByUserIdQuery(new UserId(recipientUserId))
        );

        var prefs = profileOpt.isPresent()
                ? profileOpt.get().notificationPreferences()
                : NotificationPreferences.defaults();

        var channels = prefs.channelsFor(
                NotificationType.ACCOUNT
        );

        // IN_APP
        if (channels.contains(NotificationChannel.IN_APP)) {
            notificationCommandService.handle(new CreateNotificationCommand(
                    new RecipientId(recipientUserId),
                    NotificationType.ACCOUNT,
                    NotificationChannel.IN_APP,
                    new Title(title),
                    new Body(body),
                    reference
            ));
        }

        // EMAIL
        if (channels.contains(NotificationChannel.EMAIL)) {
            var userOpt = userQueryService.handle(
                    new com.example.sistemabackenddebancos.iam.domain.model.queries.GetUserByIdQuery(
                            new com.example.sistemabackenddebancos.iam.domain.model.valueobjects.UserId(recipientUserId)
                    )
            );
            userOpt.ifPresent(u -> emailDispatcher.send(u.email().value(), title, body));
        }

        // SMS
        if (channels.contains(NotificationChannel.SMS)) {
            var pOpt = profileQueryService.handle(new GetProfileByUserIdQuery(new UserId(recipientUserId)));
            pOpt.ifPresent(p -> smsDispatcher.send(p.phoneNumber().value(), body));
        }
    }

}