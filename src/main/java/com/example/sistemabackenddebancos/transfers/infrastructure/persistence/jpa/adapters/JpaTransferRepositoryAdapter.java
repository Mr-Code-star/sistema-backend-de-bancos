package com.example.sistemabackenddebancos.transfers.infrastructure.persistence.jpa.adapters;

import com.example.sistemabackenddebancos.transfers.domain.model.aggregates.Transfer;
import com.example.sistemabackenddebancos.transfers.domain.model.valueobjects.TransferId;
import com.example.sistemabackenddebancos.transfers.domain.model.valueobjects.TransferReference;
import com.example.sistemabackenddebancos.transfers.domain.repositories.TransferRepository;
import com.example.sistemabackenddebancos.transfers.infrastructure.persistence.jpa.mappers.TransferPersistenceMapper;
import com.example.sistemabackenddebancos.transfers.infrastructure.persistence.jpa.repositories.SpringDataTransferJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaTransferRepositoryAdapter implements TransferRepository {

    private final SpringDataTransferJpaRepository jpa;

    public JpaTransferRepositoryAdapter(SpringDataTransferJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Transfer> findById(TransferId id) {
        return jpa.findById(id.value()).map(TransferPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Transfer> findByReference(TransferReference reference) {
        return jpa.findByReference(reference.value()).map(TransferPersistenceMapper::toDomain);
    }

    @Override
    public List<Transfer> findAllByAccountId(UUID accountId) {
        return jpa.findAllByFromAccountIdOrToAccountId(accountId, accountId)
                .stream()
                .map(TransferPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByReference(TransferReference reference) {
        return jpa.existsByReference(reference.value());
    }

    @Override
    public Transfer save(Transfer transfer) {
        // Si existe, actualiza; si no, crea
        var existing = jpa.findById(transfer.id().value());
        if (existing.isPresent()) {
            var entity = existing.get();
            TransferPersistenceMapper.applyToEntity(entity, transfer);
            return TransferPersistenceMapper.toDomain(jpa.save(entity));
        }
        return TransferPersistenceMapper.toDomain(jpa.save(TransferPersistenceMapper.toEntity(transfer)));
    }
}