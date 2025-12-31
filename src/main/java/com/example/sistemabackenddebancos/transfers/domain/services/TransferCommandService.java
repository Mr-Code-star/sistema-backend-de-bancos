package com.example.sistemabackenddebancos.transfers.domain.services;

import com.example.sistemabackenddebancos.transfers.domain.model.aggregates.Transfer;
import com.example.sistemabackenddebancos.transfers.domain.model.commands.CreateTransferCommand;

import java.util.Optional;

public interface TransferCommandService {
    Optional<Transfer> handle(CreateTransferCommand command);
}