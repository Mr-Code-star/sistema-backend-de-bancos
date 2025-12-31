package com.example.sistemabackenddebancos.transfers.domain.model.queries;

import com.example.sistemabackenddebancos.transfers.domain.model.valueobjects.TransferReference;

public record GetTransferByReferenceQuery(TransferReference reference) {}