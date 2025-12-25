package com.example.sistemabackenddebancos.iam.application.security.tokens;

import com.example.sistemabackenddebancos.iam.domain.model.aggregates.User;

public interface TokenService {
    String generate(User user);
}