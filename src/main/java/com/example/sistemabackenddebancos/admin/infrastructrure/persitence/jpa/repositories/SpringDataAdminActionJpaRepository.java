package com.example.sistemabackenddebancos.admin.infrastructrure.persitence.jpa.repositories;

import com.example.sistemabackenddebancos.admin.infrastructrure.persitence.jpa.entities.AdminActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataAdminActionJpaRepository extends JpaRepository<AdminActionEntity, UUID> {}