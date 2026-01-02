package com.example.sistemabackenddebancos.notifications.infrastructure.jpa.persistence.repositories;

import com.example.sistemabackenddebancos.notifications.infrastructure.jpa.persistence.entities.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataNotificationJpaRepository extends JpaRepository<NotificationEntity, UUID> {

    List<NotificationEntity> findAllByRecipientIdOrderByCreatedAtDesc(UUID recipientId);
}