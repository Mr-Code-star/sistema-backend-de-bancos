package com.example.sistemabackenddebancos.payments.domain.model.queries;

import java.util.UUID;

public record GetPaymentsByAccountIdQuery(UUID accountId) {}
