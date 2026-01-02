package com.example.sistemabackenddebancos.payments.domain.model.queries;

import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.PaymentId;

public record GetPaymentByIdQuery(PaymentId paymentId) {}
