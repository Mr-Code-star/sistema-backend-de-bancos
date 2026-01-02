package com.example.sistemabackenddebancos.payments.domain.model.queries;

import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.PaymentReference;

public record GetPaymentByReferenceQuery(PaymentReference reference) {}
