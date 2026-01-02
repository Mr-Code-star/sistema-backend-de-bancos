package com.example.sistemabackenddebancos.payments.domain.repositories;

import com.example.sistemabackenddebancos.payments.domain.model.aggregates.Payment;
import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.PaymentId;
import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.PaymentReference;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Optional<Payment> findById(PaymentId id);
    Optional<Payment> findByReference(PaymentReference reference);
    boolean existsByReference(PaymentReference reference);

    List<Payment> findAllByAccountId(UUID accountId);

    Payment save(Payment payment);
}