package com.example.sistemabackenddebancos.payments.infrastructure.persistence.jpa.mappers;

import com.example.sistemabackenddebancos.payments.domain.model.aggregates.Payment;
import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.MerchantCode;
import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.PaymentId;
import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.PaymentReference;
import com.example.sistemabackenddebancos.payments.infrastructure.persistence.jpa.entities.PaymentEntity;

import java.time.Instant;

public class PaymentPersistenceMapper {

    public static Payment toDomain(PaymentEntity e) {
        return new Payment(
                new PaymentId(e.getId()),
                new PaymentReference(e.getReference()),
                e.getFromAccountId(),
                new MerchantCode(e.getMerchantCode()),
                e.getType(),
                e.getCurrency(),
                e.getAmount(),
                e.getStatus(),
                e.getFailureReason()
        );
    }

    public static PaymentEntity toEntity(Payment p) {
        var e = new PaymentEntity();
        e.setId(p.id().value());
        e.setReference(p.reference().value());
        e.setFromAccountId(p.fromAccountId());
        e.setMerchantCode(p.merchantCode().value());
        e.setType(p.type());
        e.setCurrency(p.currency());
        e.setAmount(p.amount());
        e.setStatus(p.status());
        e.setFailureReason(p.failureReason());

        var now = Instant.now();
        e.setCreatedAt(now);
        e.setUpdatedAt(now);

        return e;
    }

    public static void applyToEntity(PaymentEntity e, Payment p) {
        e.setReference(p.reference().value());
        e.setFromAccountId(p.fromAccountId());
        e.setMerchantCode(p.merchantCode().value());
        e.setType(p.type());
        e.setCurrency(p.currency());
        e.setAmount(p.amount());
        e.setStatus(p.status());
        e.setFailureReason(p.failureReason());
        e.setUpdatedAt(Instant.now());
    }
}