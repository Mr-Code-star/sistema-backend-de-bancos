package com.example.sistemabackenddebancos.accounts.interfaces.rest.dtos.requests;

import java.math.BigDecimal;

public record AmountRequest(
        BigDecimal amount
) {}