package com.example.sistemabackenddebancos.admin.infrastructrure.persitence.jpa.repositories;

import com.example.sistemabackenddebancos.admin.infrastructrure.persitence.jpa.entities.AdminActionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataAdminActionJpaRepository extends JpaRepository<AdminActionEntity, UUID> {
    Page<AdminActionEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<AdminActionEntity> findAllByActionTypeOrderByCreatedAtDesc(String actionType, Pageable pageable);

    Page<AdminActionEntity> findAllByAdminUserIdOrderByCreatedAtDesc(UUID adminUserId, Pageable pageable);

    Page<AdminActionEntity> findAllByActionTypeAndAdminUserIdOrderByCreatedAtDesc(String actionType, UUID adminUserId, Pageable pageable);}