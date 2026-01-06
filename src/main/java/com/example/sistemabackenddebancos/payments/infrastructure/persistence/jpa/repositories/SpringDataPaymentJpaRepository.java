package com.example.sistemabackenddebancos.payments.infrastructure.persistence.jpa.repositories;

import com.example.sistemabackenddebancos.payments.infrastructure.persistence.jpa.entities.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataPaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {
    Optional<PaymentEntity> findByReference(String reference);

    boolean existsByReference(String reference);

    List<PaymentEntity> findAllByFromAccountIdOrderByCreatedAtDesc(UUID fromAccountId);
}
