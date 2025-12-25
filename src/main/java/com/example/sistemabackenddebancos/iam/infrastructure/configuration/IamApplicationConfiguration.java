package com.example.sistemabackenddebancos.iam.infrastructure.configuration;

import com.example.sistemabackenddebancos.iam.application.security.hashing.PasswordHasher;
import com.example.sistemabackenddebancos.iam.application.security.mfa.TotpService;
import com.example.sistemabackenddebancos.iam.application.security.tokens.TokenService;
import com.example.sistemabackenddebancos.iam.application.services.UserCommandServiceImpl;
import com.example.sistemabackenddebancos.iam.application.services.UserQueryServiceImpl;
import com.example.sistemabackenddebancos.iam.domain.repositories.UserRepository;
import com.example.sistemabackenddebancos.iam.domain.services.UserCommandService;
import com.example.sistemabackenddebancos.iam.domain.services.UserQueryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IamApplicationConfiguration {

    @Bean
    public UserCommandService userCommandService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            TokenService tokenService,
            TotpService totpService

    ) {
        return new UserCommandServiceImpl(userRepository, passwordHasher, tokenService, totpService);
    }

    @Bean
    public UserQueryService userQueryService(UserRepository userRepository) {
        return new UserQueryServiceImpl(userRepository);
    }
}