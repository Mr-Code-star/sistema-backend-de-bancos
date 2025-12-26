package com.example.sistemabackenddebancos.iam.application.security.mfa;

public interface EmailService {
    void sendVerificationCode(String email, String code);
    boolean isValidEmail(String email);
}