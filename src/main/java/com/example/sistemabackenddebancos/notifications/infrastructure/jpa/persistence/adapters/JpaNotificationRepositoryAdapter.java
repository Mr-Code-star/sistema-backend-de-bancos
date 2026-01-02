package com.example.sistemabackenddebancos.notifications.infrastructure.jpa.persistence.adapters;

import com.example.sistemabackenddebancos.notifications.domain.model.aggregates.Notification;
import com.example.sistemabackenddebancos.notifications.domain.model.valueobjects.NotificationId;
import com.example.sistemabackenddebancos.notifications.domain.model.valueobjects.RecipientId;
import com.example.sistemabackenddebancos.notifications.domain.repositories.NotificationRepository;
import com.example.sistemabackenddebancos.notifications.infrastructure.jpa.persistence.mappers.NotificationPersistenceMapper;
import com.example.sistemabackenddebancos.notifications.infrastructure.jpa.persistence.repositories.SpringDataNotificationJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaNotificationRepositoryAdapter implements NotificationRepository {

    private final SpringDataNotificationJpaRepository jpa;

    public JpaNotificationRepositoryAdapter(SpringDataNotificationJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Notification save(Notification notification) {
        var saved = jpa.save(NotificationPersistenceMapper.toEntity(notification));
        return NotificationPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Notification> findById(NotificationId id) {
        return jpa.findById(id.value()).map(NotificationPersistenceMapper::toDomain);
    }

    @Override
    public List<Notification> findAllByRecipientId(RecipientId recipientId) {
        return jpa.findAllByRecipientIdOrderByCreatedAtDesc(recipientId.value())
                .stream()
                .map(NotificationPersistenceMapper::toDomain)
                .toList();
    }
}