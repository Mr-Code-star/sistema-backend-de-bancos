package com.example.sistemabackenddebancos.payments.domain.services;

import com.example.sistemabackenddebancos.payments.domain.model.aggregates.Payment;
import com.example.sistemabackenddebancos.payments.domain.model.commands.CreatePaymentCommand;

import java.util.Optional;

public interface PaymentCommandService {
    Optional<Payment> handle(CreatePaymentCommand command);
}