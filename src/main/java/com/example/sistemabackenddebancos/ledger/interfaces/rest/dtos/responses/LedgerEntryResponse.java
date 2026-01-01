package com.example.sistemabackenddebancos.ledger.interfaces.rest.dtos.responses;

public record LedgerEntryResponse(
        String entryId,
        String accountId,
        String type,      // DEBIT/CREDIT
        String source,    // DEPOSIT/WITHDRAW/TRANSFER
        String currency,  // PEN/USD
        String amount,
        String reference,
        String createdAt
) {}