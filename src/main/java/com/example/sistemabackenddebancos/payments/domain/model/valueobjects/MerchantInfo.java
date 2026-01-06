package com.example.sistemabackenddebancos.payments.domain.model.valueobjects;

import com.example.sistemabackenddebancos.payments.domain.model.enumerations.Category;

public record MerchantInfo(
        String code,
        String name,
        Category category
) {}