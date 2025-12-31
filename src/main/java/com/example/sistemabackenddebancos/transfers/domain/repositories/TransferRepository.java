package com.example.sistemabackenddebancos.transfers.domain.repositories;

import com.example.sistemabackenddebancos.transfers.domain.model.aggregates.Transfer;
import com.example.sistemabackenddebancos.transfers.domain.model.valueobjects.TransferId;
import com.example.sistemabackenddebancos.transfers.domain.model.valueobjects.TransferReference;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransferRepository {
    Optional<Transfer> findById(TransferId id);
    Optional<Transfer> findByReference(TransferReference reference);

    List<Transfer> findAllByAccountId(UUID accountId);

    boolean existsByReference(TransferReference reference);

    Transfer save(Transfer transfer);
}