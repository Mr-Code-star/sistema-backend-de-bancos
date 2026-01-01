package com.example.sistemabackenddebancos.ledger.domain.model.aggregates;

import com.example.sistemabackenddebancos.ledger.domain.model.enumerations.EntrySource;
import com.example.sistemabackenddebancos.ledger.domain.model.enumerations.EntryType;
import com.example.sistemabackenddebancos.ledger.domain.model.valueobjects.EntryId;
import com.example.sistemabackenddebancos.ledger.domain.model.valueobjects.TransactionReference;
import com.example.sistemabackenddebancos.shared.domain.model.enumerations.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class LedgerEntry {

    private final EntryId id;
    private final UUID accountId;

    private final EntryType type;       // DEBIT / CREDIT
    private final EntrySource source;   // DEPOSIT / WITHDRAW / TRANSFER

    private final Currency currency;
    private final BigDecimal amount;

    private final TransactionReference reference; // correlación
    private final Instant createdAt;

    public LedgerEntry(EntryId id,
                       UUID accountId,
                       EntryType type,
                       EntrySource source,
                       Currency currency,
                       BigDecimal amount,
                       TransactionReference reference,
                       Instant createdAt) {

        this.id = Objects.requireNonNull(id, "EntryId cannot be null");
        this.accountId = Objects.requireNonNull(accountId, "accountId cannot be null");
        this.type = Objects.requireNonNull(type, "EntryType cannot be null");
        this.source = Objects.requireNonNull(source, "EntrySource cannot be null");
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null");

        this.amount = Objects.requireNonNull(amount, "amount cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("amount must be > 0");

        this.reference = Objects.requireNonNull(reference, "TransactionReference cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
    }

    /* ===== Factory methods ===== */

    public static LedgerEntry credit(UUID accountId,
                                     Currency currency,
                                     BigDecimal amount,
                                     EntrySource source,
                                     TransactionReference reference) {

        return new LedgerEntry(
                EntryId.newId(),
                accountId,
                EntryType.CREDIT,
                source,
                currency,
                amount,
                reference,
                Instant.now()
        );
    }

    public static LedgerEntry debit(UUID accountId,
                                    Currency currency,
                                    BigDecimal amount,
                                    EntrySource source,
                                    TransactionReference reference) {

        return new LedgerEntry(
                EntryId.newId(),
                accountId,
                EntryType.DEBIT,
                source,
                currency,
                amount,
                reference,
                Instant.now()
        );
    }

    /* ===== Getters ===== */

    public EntryId id() { return id; }
    public UUID accountId() { return accountId; }
    public EntryType type() { return type; }
    public EntrySource source() { return source; }
    public Currency currency() { return currency; }
    public BigDecimal amount() { return amount; }
    public TransactionReference reference() { return reference; }
    public Instant createdAt() { return createdAt; }
}