package com.example.sistemabackenddebancos.limits.infrastructure.persistence.jpa.repositories;

import com.example.sistemabackenddebancos.limits.domain.model.enumerations.OperationType;
import com.example.sistemabackenddebancos.limits.infrastructure.persistence.jpa.entities.LimitOverrideEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataLimitOverrideJpaRepository extends JpaRepository<LimitOverrideEntity, UUID> {

    Optional<LimitOverrideEntity> findByUserIdAndOperationType(UUID userId, OperationType operationType);
}