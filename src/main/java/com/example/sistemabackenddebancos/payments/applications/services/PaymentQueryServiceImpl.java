package com.example.sistemabackenddebancos.payments.applications.services;

import com.example.sistemabackenddebancos.payments.domain.model.aggregates.Payment;
import com.example.sistemabackenddebancos.payments.domain.model.queries.*;
import com.example.sistemabackenddebancos.payments.domain.repositories.PaymentRepository;
import com.example.sistemabackenddebancos.payments.domain.services.PaymentQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentQueryServiceImpl implements PaymentQueryService {

    private final PaymentRepository paymentRepository;

    public PaymentQueryServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Optional<Payment> handle(GetPaymentByIdQuery query) {
        return paymentRepository.findById(query.paymentId());
    }

    @Override
    public Optional<Payment> handle(GetPaymentByReferenceQuery query) {
        return paymentRepository.findByReference(query.reference());
    }

    @Override
    public List<Payment> handle(GetPaymentsByAccountIdQuery query) {
        return paymentRepository.findAllByAccountId(query.accountId());
    }
}