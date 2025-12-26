package com.example.sistemabackenddebancos.iam.infrastructure.security.mfa;

import com.example.sistemabackenddebancos.iam.application.security.mfa.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public boolean isValidEmail(String email) {
        return email != null &&
                email.contains("@") &&
                email.contains(".") &&
                email.length() > 5;
    }

    @Override
    public void sendVerificationCode(String email, String code) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Email inválido");
        }

        // SIMULACIÓN - En producción usarías JavaMail, etc.
        log.info("📧 ENVIANDO EMAIL a {}: Código de verificación: {}", email, code);

        System.out.println("\n=========================================");
        System.out.println("📨 EMAIL SIMULADO");
        System.out.println("Para: " + email);
        System.out.println("Código: " + code);
        System.out.println("=========================================\n");
    }
}