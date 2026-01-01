package com.example.sistemabackenddebancos.ledger.domain.model.commands;

import com.example.sistemabackenddebancos.ledger.domain.model.enumerations.EntrySource;
import com.example.sistemabackenddebancos.ledger.domain.model.enumerations.EntryType;
import com.example.sistemabackenddebancos.ledger.domain.model.valueobjects.TransactionReference;
import com.example.sistemabackenddebancos.shared.domain.model.enumerations.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record PostLedgerEntryCommand(
        UUID accountId,
        EntryType type,
        EntrySource source,
        Currency currency,
        BigDecimal amount,
        TransactionReference reference
) {}