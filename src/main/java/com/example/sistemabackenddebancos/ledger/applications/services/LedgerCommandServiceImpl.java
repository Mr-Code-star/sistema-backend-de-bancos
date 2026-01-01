package com.example.sistemabackenddebancos.ledger.applications.services;

import com.example.sistemabackenddebancos.ledger.domain.model.aggregates.LedgerEntry;
import com.example.sistemabackenddebancos.ledger.domain.model.commands.PostLedgerEntryCommand;
import com.example.sistemabackenddebancos.ledger.domain.model.enumerations.EntryType;
import com.example.sistemabackenddebancos.ledger.domain.repositories.LedgerRepository;
import com.example.sistemabackenddebancos.ledger.domain.services.LedgerCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LedgerCommandServiceImpl implements LedgerCommandService {

    private final LedgerRepository ledgerRepository;

    public LedgerCommandServiceImpl(LedgerRepository ledgerRepository) {
        this.ledgerRepository = ledgerRepository;
    }

    @Override
    @Transactional
    public LedgerEntry handle(PostLedgerEntryCommand command) {

        LedgerEntry entry;

        if (command.type() == EntryType.CREDIT) {
            entry = LedgerEntry.credit(
                    command.accountId(),
                    command.currency(),
                    command.amount(),
                    command.source(),
                    command.reference()
            );
        } else {
            entry = LedgerEntry.debit(
                    command.accountId(),
                    command.currency(),
                    command.amount(),
                    command.source(),
                    command.reference()
            );
        }

        return ledgerRepository.save(entry);
    }
}