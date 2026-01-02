package com.example.sistemabackenddebancos.payments.domain.model.aggregates;

import com.example.sistemabackenddebancos.payments.domain.model.enumerations.PaymentStatus;
import com.example.sistemabackenddebancos.payments.domain.model.enumerations.PaymentType;
import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.MerchantCode;
import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.PaymentId;
import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.PaymentReference;
import com.example.sistemabackenddebancos.shared.domain.model.enumerations.Currency;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Payment {

    private final PaymentId id;
    private final PaymentReference reference;

    private final UUID fromAccountId;
    private final MerchantCode merchantCode;
    private final PaymentType type;

    private final Currency currency;
    private final BigDecimal amount;

    private final PaymentStatus status;
    private final String failureReason; // optional

    public Payment(PaymentId id,
                   PaymentReference reference,
                   UUID fromAccountId,
                   MerchantCode merchantCode,
                   PaymentType type,
                   Currency currency,
                   BigDecimal amount,
                   PaymentStatus status,
                   String failureReason) {

        this.id = Objects.requireNonNull(id, "Payment.id cannot be null");
        this.reference = Objects.requireNonNull(reference, "Payment.reference cannot be null");
        this.fromAccountId = Objects.requireNonNull(fromAccountId, "fromAccountId cannot be null");
        this.merchantCode = Objects.requireNonNull(merchantCode, "merchantCode cannot be null");
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.currency = Objects.requireNonNull(currency, "currency cannot be null");

        this.amount = Objects.requireNonNull(amount, "amount cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("amount must be > 0");

        this.status = Objects.requireNonNull(status, "status cannot be null");
        this.failureReason = (failureReason == null || failureReason.isBlank()) ? null : failureReason.trim();
    }

    public static Payment createNew(PaymentReference reference,
                                    UUID fromAccountId,
                                    MerchantCode merchantCode,
                                    PaymentType type,
                                    Currency currency,
                                    BigDecimal amount) {

        return new Payment(
                PaymentId.newId(),
                reference,
                fromAccountId,
                merchantCode,
                type,
                currency,
                amount,
                PaymentStatus.PENDING,
                null
        );
    }

    public PaymentId id() { return id; }
    public PaymentReference reference() { return reference; }
    public UUID fromAccountId() { return fromAccountId; }
    public MerchantCode merchantCode() { return merchantCode; }
    public PaymentType type() { return type; }
    public Currency currency() { return currency; }
    public BigDecimal amount() { return amount; }
    public PaymentStatus status() { return status; }
    public String failureReason() { return failureReason; }

    public Payment markCompleted() {
        if (status == PaymentStatus.COMPLETED) return this;
        if (status == PaymentStatus.FAILED) throw new IllegalStateException("Cannot complete a failed payment");
        return new Payment(id, reference, fromAccountId, merchantCode, type, currency, amount, PaymentStatus.COMPLETED, null);
    }

    public Payment markFailed(String reason) {
        if (status == PaymentStatus.FAILED) return this;
        if (status == PaymentStatus.COMPLETED) throw new IllegalStateException("Cannot fail a completed payment");
        var r = (reason == null || reason.isBlank()) ? "Payment failed" : reason.trim();
        return new Payment(id, reference, fromAccountId, merchantCode, type, currency, amount, PaymentStatus.FAILED, r);
    }
}