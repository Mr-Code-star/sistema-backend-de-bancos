package com.example.sistemabackenddebancos.transfers.interfaces.rest.dtos.responses;

public record TransferResponse(
        String transferId,
        String reference,
        String fromAccountId,
        String toAccountId,
        String currency,
        String amount,
        String status,
        String failureReason
) {}