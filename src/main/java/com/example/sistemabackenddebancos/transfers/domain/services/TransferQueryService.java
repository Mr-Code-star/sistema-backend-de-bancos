package com.example.sistemabackenddebancos.transfers.domain.services;

import com.example.sistemabackenddebancos.transfers.domain.model.aggregates.Transfer;
import com.example.sistemabackenddebancos.transfers.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

public interface TransferQueryService {
    Optional<Transfer> handle(GetTransferByIdQuery query);
    Optional<Transfer> handle(GetTransferByReferenceQuery query);
    List<Transfer> handle(GetTransfersByAccountIdQuery query);
}