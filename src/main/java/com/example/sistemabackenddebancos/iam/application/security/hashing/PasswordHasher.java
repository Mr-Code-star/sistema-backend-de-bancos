package com.example.sistemabackenddebancos.iam.application.security.hashing;

public interface PasswordHasher {
    String hash(String plain);
    boolean matches(String plain, String hashed);
}