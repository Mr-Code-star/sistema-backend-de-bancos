package com.example.sistemabackenddebancos.ledger.infrastructure.persistence.jpa.entities;

import com.example.sistemabackenddebancos.ledger.domain.model.enumerations.EntrySource;
import com.example.sistemabackenddebancos.ledger.domain.model.enumerations.EntryType;
import com.example.sistemabackenddebancos.shared.domain.model.enumerations.Currency;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntryEntity {

    @Id
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryType type;     // DEBIT/CREDIT

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntrySource source; // DEPOSIT/WITHDRAW/TRANSFER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "reference", nullable = false)
    private String reference;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public LedgerEntryEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAccountId() { return accountId; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }

    public EntryType getType() { return type; }
    public void setType(EntryType type) { this.type = type; }

    public EntrySource getSource() { return source; }
    public void setSource(EntrySource source) { this.source = source; }

    public Currency getCurrency() { return currency; }
    public void setCurrency(Currency currency) { this.currency = currency; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}