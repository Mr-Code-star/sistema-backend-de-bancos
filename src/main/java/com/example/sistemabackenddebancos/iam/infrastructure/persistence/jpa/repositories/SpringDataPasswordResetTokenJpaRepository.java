package com.example.sistemabackenddebancos.iam.infrastructure.persistence.jpa.repositories;

import com.example.sistemabackenddebancos.iam.domain.model.enumerations.PasswordResetTokenStatus;
import com.example.sistemabackenddebancos.iam.infrastructure.persistence.jpa.entities.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataPasswordResetTokenJpaRepository extends JpaRepository<PasswordResetTokenEntity, UUID> {
    Optional<PasswordResetTokenEntity> findByTokenHashAndStatus(String tokenHash, PasswordResetTokenStatus status);
}