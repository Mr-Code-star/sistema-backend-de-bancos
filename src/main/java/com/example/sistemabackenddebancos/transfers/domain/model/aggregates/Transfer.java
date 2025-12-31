package com.example.sistemabackenddebancos.transfers.domain.model.aggregates;

import com.example.sistemabackenddebancos.shared.domain.model.enumerations.Currency;
import com.example.sistemabackenddebancos.transfers.domain.model.enumerations.TransferStatus;
import com.example.sistemabackenddebancos.transfers.domain.model.valueobjects.TransferId;
import com.example.sistemabackenddebancos.transfers.domain.model.valueobjects.TransferReference;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Transfer {

    private final TransferId id;
    private final TransferReference reference;

    private final UUID fromAccountId;
    private final UUID toAccountId;

    private final Currency currency;
    private final BigDecimal amount;

    private final TransferStatus status;
    private final String failureReason; // opcional

    public Transfer(TransferId id,
                    TransferReference reference,
                    UUID fromAccountId,
                    UUID toAccountId,
                    Currency currency,
                    BigDecimal amount,
                    TransferStatus status,
                    String failureReason) {

        this.id = Objects.requireNonNull(id, "Transfer.id cannot be null");
        this.reference = Objects.requireNonNull(reference, "Transfer.reference cannot be null");
        this.fromAccountId = Objects.requireNonNull(fromAccountId, "fromAccountId cannot be null");
        this.toAccountId = Objects.requireNonNull(toAccountId, "toAccountId cannot be null");
        if (fromAccountId.equals(toAccountId)) throw new IllegalArgumentException("fromAccountId and toAccountId cannot be the same");

        this.currency = Objects.requireNonNull(currency, "currency cannot be null");
        this.amount = Objects.requireNonNull(amount, "amount cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("amount must be > 0");

        this.status = Objects.requireNonNull(status, "status cannot be null");
        this.failureReason = (failureReason == null || failureReason.isBlank()) ? null : failureReason.trim();
    }

    public static Transfer createNew(TransferReference reference,
                                     UUID fromAccountId,
                                     UUID toAccountId,
                                     Currency currency,
                                     BigDecimal amount) {
        return new Transfer(
                TransferId.newId(),
                reference,
                fromAccountId,
                toAccountId,
                currency,
                amount,
                TransferStatus.PENDING,
                null
        );
    }

    public TransferId id() { return id; }
    public TransferReference reference() { return reference; }
    public UUID fromAccountId() { return fromAccountId; }
    public UUID toAccountId() { return toAccountId; }
    public Currency currency() { return currency; }
    public BigDecimal amount() { return amount; }
    public TransferStatus status() { return status; }
    public String failureReason() { return failureReason; }

    // metodos

    public Transfer markCompleted() {
        if (status == TransferStatus.COMPLETED) return this;
        if (status == TransferStatus.FAILED) throw new IllegalStateException("Cannot complete a failed transfer");
        return new Transfer(id, reference, fromAccountId, toAccountId, currency, amount, TransferStatus.COMPLETED, null);
    }

    public Transfer markFailed(String reason) {
        if (status == TransferStatus.FAILED) return this;
        if (status == TransferStatus.COMPLETED) throw new IllegalStateException("Cannot fail a completed transfer");
        var r = (reason == null || reason.isBlank()) ? "Transfer failed" : reason.trim();
        return new Transfer(id, reference, fromAccountId, toAccountId, currency, amount, TransferStatus.FAILED, r);
    }
}