package com.example.sistemabackenddebancos.payments.domain.model.commands;

import com.example.sistemabackenddebancos.payments.domain.model.enumerations.PaymentType;
import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.MerchantCode;
import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.PaymentReference;
import com.example.sistemabackenddebancos.shared.domain.model.enumerations.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentCommand(
        PaymentReference reference,
        UUID fromAccountId,
        MerchantCode merchantCode,
        PaymentType type,
        Currency currency,
        BigDecimal amount,
        String customerRef
) {}