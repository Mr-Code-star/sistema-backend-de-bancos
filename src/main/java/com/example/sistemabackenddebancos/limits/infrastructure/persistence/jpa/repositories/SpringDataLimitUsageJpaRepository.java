package com.example.sistemabackenddebancos.limits.infrastructure.persistence.jpa.repositories;

import com.example.sistemabackenddebancos.limits.domain.model.enumerations.OperationType;
import com.example.sistemabackenddebancos.limits.infrastructure.persistence.jpa.entities.LimitUsageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataLimitUsageJpaRepository extends JpaRepository<LimitUsageEntity, UUID> {

    Optional<LimitUsageEntity> findByUserIdAndOperationTypeAndDay(UUID userId, OperationType operationType, LocalDate day);
}
