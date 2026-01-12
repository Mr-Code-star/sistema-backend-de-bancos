package com.example.sistemabackenddebancos.statements.application.calculators;

import com.example.sistemabackenddebancos.ledger.domain.model.aggregates.LedgerEntry;
import com.example.sistemabackenddebancos.ledger.domain.model.enumerations.EntryType;
import com.example.sistemabackenddebancos.statements.domain.model.aggregates.Statement;
import com.example.sistemabackenddebancos.statements.domain.model.valueobjects.DateRange;
import com.example.sistemabackenddebancos.statements.domain.model.valueobjects.StatementLine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StatementCalculator {

    public static BigDecimal calculateBalanceFromEntries(List<LedgerEntry> entries) {
        BigDecimal credits = BigDecimal.ZERO;
        BigDecimal debits = BigDecimal.ZERO;

        for (var e : entries) {
            if (e.type() == EntryType.CREDIT) credits = credits.add(e.amount());
            else debits = debits.add(e.amount());
        }
        return credits.subtract(debits);
    }

    public static Statement build(UUID accountId,
                                  DateRange range,
                                  String currency,
                                  BigDecimal openingBalance,
                                  List<LedgerEntry> inRange) {

        BigDecimal totalCredits = BigDecimal.ZERO;
        BigDecimal totalDebits = BigDecimal.ZERO;

        List<StatementLine> lines = new ArrayList<>(inRange.size());

        for (var e : inRange) {
            if (e.type() == EntryType.CREDIT) totalCredits = totalCredits.add(e.amount());
            else totalDebits = totalDebits.add(e.amount());

            lines.add(new StatementLine(
                    e.createdAt(),
                    e.accountId(),
                    e.type().name(),
                    e.source().name(),
                    e.amount(),
                    e.currency().name(),
                    e.reference().value()
            ));
        }

        BigDecimal closingBalance = openingBalance.add(totalCredits.subtract(totalDebits));

        return new Statement(
                accountId,
                range,
                openingBalance,
                totalCredits,
                totalDebits,
                closingBalance,
                currency,
                lines
        );
    }
}