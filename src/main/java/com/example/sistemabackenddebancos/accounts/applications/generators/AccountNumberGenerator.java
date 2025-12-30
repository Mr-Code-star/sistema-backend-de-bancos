package com.example.sistemabackenddebancos.accounts.applications.generators;

import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountNumber;

public interface AccountNumberGenerator {
    AccountNumber generate();
}