package com.example.sistemabackenddebancos.accounts.infrastructure.persistence.jpa.repositories;

import com.example.sistemabackenddebancos.accounts.infrastructure.persistence.jpa.entities.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataAccountJpaRepository extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
    List<AccountEntity> findAllByOwnerId(UUID ownerId);
}