package com.example.sistemabackenddebancos.statements.application.services;

import com.example.sistemabackenddebancos.accounts.domain.model.queries.GetAccountByIdQuery;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountId;
import com.example.sistemabackenddebancos.accounts.domain.services.AccountQueryService;
import com.example.sistemabackenddebancos.ledger.domain.repositories.LedgerRepository;
import com.example.sistemabackenddebancos.statements.application.calculators.StatementCalculator;
import com.example.sistemabackenddebancos.statements.domain.model.aggregates.Statement;
import com.example.sistemabackenddebancos.statements.domain.model.queries.GetFullStatementByAccountQuery;
import com.example.sistemabackenddebancos.statements.domain.model.queries.GetStatementByAccountQuery;
import com.example.sistemabackenddebancos.statements.domain.services.StatementQueryService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class StatementQueryServiceImpl implements StatementQueryService {

    private final LedgerRepository ledgerRepository;
    private final AccountQueryService accountQueryService;

    public StatementQueryServiceImpl(LedgerRepository ledgerRepository, AccountQueryService accountQueryService) {
        this.ledgerRepository = ledgerRepository;
        this.accountQueryService = accountQueryService;
    }

    @Override
    public Statement handle(GetStatementByAccountQuery query) {
        UUID accountId = query.accountId();

        // Obtener currency de la cuenta
        var accOpt = accountQueryService.handle(new GetAccountByIdQuery(new AccountId(accountId)));

        if (accOpt.isEmpty()) throw new IllegalArgumentException("Account not found");

        var currency = accOpt.get().currency().name();

        // Rango de fechas -> instants (zona UTC)

        LocalDate fromDate = query.range().from();
        LocalDate toDate = query.range().to();

        Instant fromInclusive = fromDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant toExclusive = toDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        // Opening balance = suma histórica hasta before from
        var beforeEntries = ledgerRepository.findAllByAccountIdBefore(accountId, fromInclusive);
        BigDecimal openingBalance = StatementCalculator.calculateBalanceFromEntries(beforeEntries);

        // Entradas dentro del rango
        var inRange = ledgerRepository.findAllByAccountIdBetween(accountId, fromInclusive, toExclusive);

        return StatementCalculator.build(accountId, query.range(), currency, openingBalance, inRange);
    }

    @Override
    public Statement handle(GetFullStatementByAccountQuery query) {
        var accOpt = accountQueryService.handle(new GetAccountByIdQuery(new AccountId(query.accountId())));
        if (accOpt.isEmpty()) throw new IllegalArgumentException("Account not found");

        var currency = accOpt.get().currency().name();

        var allEntries = ledgerRepository.findAllByAccountId(query.accountId());

        // openingBalance = 0 (all-time)
        var opening = java.math.BigDecimal.ZERO;

        // DateRange “virtual” (opcional)
        var range = new com.example.sistemabackenddebancos.statements.domain.model.valueobjects.DateRange(
                java.time.LocalDate.of(1970, 1, 1),
                java.time.LocalDate.now()
        );

        return StatementCalculator.build(query.accountId(), range, currency, opening, allEntries);    }
}
