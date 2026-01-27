package com.example.sistemabackenddebancos.admin.application.services;

import com.example.sistemabackenddebancos.admin.infrastructrure.persitence.jpa.entities.AdminActionEntity;
import com.example.sistemabackenddebancos.admin.infrastructrure.persitence.jpa.repositories.SpringDataAdminActionJpaRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AdminAuditService {
    private final SpringDataAdminActionJpaRepository repo;

    public AdminAuditService(SpringDataAdminActionJpaRepository repo) {
        this.repo = repo;
    }

    public void log(UUID adminUserId, String ActionType, String TargetType, UUID targetId, String reason ) {
        var e = new AdminActionEntity();
        e.setId(UUID.randomUUID());
        e.setAdminUserId(adminUserId);
        e.setActionType(ActionType);
        e.setTargetType(TargetType);
        e.setTargetId(targetId);
        e.setReason(reason);
        e.setCreatedAt(Instant.now());
        repo.save(e);
    }
}
