package com.example.sistemabackenddebancos.transfers.domain.model.queries;


import com.example.sistemabackenddebancos.transfers.domain.model.valueobjects.TransferId;

public record GetTransferByIdQuery(TransferId transferId) {}