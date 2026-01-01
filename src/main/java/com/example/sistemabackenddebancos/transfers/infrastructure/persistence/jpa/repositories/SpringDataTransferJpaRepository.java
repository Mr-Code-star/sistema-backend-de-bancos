package com.example.sistemabackenddebancos.transfers.infrastructure.persistence.jpa.repositories;

import com.example.sistemabackenddebancos.transfers.infrastructure.persistence.jpa.entities.TransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataTransferJpaRepository extends JpaRepository<TransferEntity, UUID> {

    Optional<TransferEntity> findByReference(String reference);

    boolean existsByReference(String reference);

    // Internas: si la cuenta fue origen o destino, la consideramos en el historial
    List<TransferEntity> findAllByFromAccountIdOrToAccountId(UUID fromAccountId, UUID toAccountId);
}