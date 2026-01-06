package com.example.sistemabackenddebancos.payments.infrastructure.persistence.jpa.adapters;

import com.example.sistemabackenddebancos.payments.domain.model.aggregates.Payment;
import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.PaymentId;
import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.PaymentReference;
import com.example.sistemabackenddebancos.payments.domain.repositories.PaymentRepository;
import com.example.sistemabackenddebancos.payments.infrastructure.persistence.jpa.mappers.PaymentPersistenceMapper;
import com.example.sistemabackenddebancos.payments.infrastructure.persistence.jpa.repositories.SpringDataPaymentJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaPaymentRepositoryAdapter implements PaymentRepository {

    private final SpringDataPaymentJpaRepository jpa;

    public JpaPaymentRepositoryAdapter(SpringDataPaymentJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Payment> findById(PaymentId id) {
        return jpa.findById(id.value()).map(PaymentPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Payment> findByReference(PaymentReference reference) {
        return jpa.findByReference(reference.value()).map(PaymentPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByReference(PaymentReference reference) {
        return jpa.existsByReference(reference.value());
    }

    @Override
    public List<Payment> findAllByAccountId(UUID accountId) {
        return jpa.findAllByFromAccountIdOrderByCreatedAtDesc(accountId)
                .stream()
                .map(PaymentPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Payment save(Payment payment) {
        var existing = jpa.findById(payment.id().value());
        if (existing.isPresent()) {
            var entity = existing.get();
            PaymentPersistenceMapper.applyToEntity(entity, payment);
            return PaymentPersistenceMapper.toDomain(jpa.save(entity));
        }
        return PaymentPersistenceMapper.toDomain(jpa.save(PaymentPersistenceMapper.toEntity(payment)));
    }
}