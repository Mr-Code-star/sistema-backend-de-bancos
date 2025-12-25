package com.example.sistemabackenddebancos.iam.infrastructure.security.hashing;

import com.example.sistemabackenddebancos.iam.application.security.hashing.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String plain) {
        return encoder.encode(plain);
    }

    @Override
    public boolean matches(String plain, String hashed) {
        return encoder.matches(plain, hashed);
    }
}