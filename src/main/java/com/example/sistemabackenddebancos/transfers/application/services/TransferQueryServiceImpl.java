package com.example.sistemabackenddebancos.transfers.application.services;

import com.example.sistemabackenddebancos.transfers.domain.model.aggregates.Transfer;
import com.example.sistemabackenddebancos.transfers.domain.model.queries.*;
import com.example.sistemabackenddebancos.transfers.domain.repositories.TransferRepository;
import com.example.sistemabackenddebancos.transfers.domain.services.TransferQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransferQueryServiceImpl implements TransferQueryService {

    private final TransferRepository transferRepository;

    public TransferQueryServiceImpl(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    @Override
    public Optional<Transfer> handle(GetTransferByIdQuery query) {
        return transferRepository.findById(query.transferId());
    }

    @Override
    public Optional<Transfer> handle(GetTransferByReferenceQuery query) {
        return transferRepository.findByReference(query.reference());
    }

    @Override
    public List<Transfer> handle(GetTransfersByAccountIdQuery query) {
        return transferRepository.findAllByAccountId(query.accountId());
    }
}