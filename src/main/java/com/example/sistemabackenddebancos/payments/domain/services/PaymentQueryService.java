package com.example.sistemabackenddebancos.payments.domain.services;

import com.example.sistemabackenddebancos.payments.domain.model.aggregates.Payment;
import com.example.sistemabackenddebancos.payments.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

public interface PaymentQueryService {
    Optional<Payment> handle(GetPaymentByIdQuery query);
    Optional<Payment> handle(GetPaymentByReferenceQuery query);
    List<Payment> handle(GetPaymentsByAccountIdQuery query);
}