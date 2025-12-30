package com.example.sistemabackenddebancos.accounts.infrastructure.persistence.jpa.generators;

import com.example.sistemabackenddebancos.accounts.applications.generators.AccountNumberGenerator;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountNumber;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RandomAccountNumberGenerator implements AccountNumberGenerator {

    private final SecureRandom random = new SecureRandom();

    @Override
    public AccountNumber generate() {
        // 14 dígitos (puedes ajustar): 10-20 según tu VO
        StringBuilder sb = new StringBuilder(14);
        for (int i = 0; i < 14; i++) {
            sb.append(random.nextInt(10));
        }
        return new AccountNumber(sb.toString());
    }
}