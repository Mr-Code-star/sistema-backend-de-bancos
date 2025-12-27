package com.example.sistemabackenddebancos.profile.infrastructure.persistence.jpa.repositories;

import com.example.sistemabackenddebancos.profile.infrastructure.persistence.jpa.entities.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataProfileJpaRepository extends JpaRepository<ProfileEntity, UUID> {
    Optional<ProfileEntity> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}
