package com.example.sistemabackenddebancos.ledger.domain.services;

import com.example.sistemabackenddebancos.ledger.domain.model.aggregates.LedgerEntry;
import com.example.sistemabackenddebancos.ledger.domain.model.commands.PostLedgerEntryCommand;

public interface LedgerCommandService {
    LedgerEntry handle(PostLedgerEntryCommand command);
}