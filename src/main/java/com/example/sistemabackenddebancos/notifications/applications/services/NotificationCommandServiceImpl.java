package com.example.sistemabackenddebancos.notifications.applications.services;

import com.example.sistemabackenddebancos.notifications.domain.model.aggregates.Notification;
import com.example.sistemabackenddebancos.notifications.domain.model.commands.ArchiveNotificationCommand;
import com.example.sistemabackenddebancos.notifications.domain.model.commands.CreateNotificationCommand;
import com.example.sistemabackenddebancos.notifications.domain.model.commands.MarkAsReadCommand;
import com.example.sistemabackenddebancos.notifications.domain.repositories.NotificationRepository;
import com.example.sistemabackenddebancos.notifications.domain.services.NotificationCommandService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationCommandServiceImpl implements NotificationCommandService {

    private final NotificationRepository notificationRepository;

    public NotificationCommandServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public Notification handle(CreateNotificationCommand command) {
        var notification = Notification.create(
                command.recipientId(),
                command.type(),
                command.channel(),
                command.title(),
                command.body(),
                command.reference()
        );
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public Optional<Notification> handle(MarkAsReadCommand command) {
        var opt = notificationRepository.findById(command.notificationId());
        if (opt.isEmpty()) return Optional.empty();

        var updated = opt.get().markRead();
        return Optional.of(notificationRepository.save(updated));
    }

    @Override
    @Transactional
    public Optional<Notification> handle(ArchiveNotificationCommand command) {
        var opt = notificationRepository.findById(command.notificationId());
        if (opt.isEmpty()) return Optional.empty();

        var updated = opt.get().archive();
        return Optional.of(notificationRepository.save(updated));
    }
}