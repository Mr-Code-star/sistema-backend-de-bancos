package com.example.sistemabackenddebancos.iam.application.security.mfa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PeruSmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(PeruSmsServiceImpl.class);

    @Override
    public boolean isValidPhoneNumber(String phoneNumber) {
        // Formato peruano: +51 9XX XXX XXX (12 dígitos)
        return phoneNumber != null &&
                phoneNumber.startsWith("+51") &&
                phoneNumber.length() == 12 && // +51 9 12345678
                phoneNumber.matches("\\+51[0-9]{9}");
    }

    @Override
    public void sendVerificationCode(String phoneNumber, String code) {
        if (!isValidPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Número peruano inválido. Formato: +519XXXXXXXX (12 dígitos)");
        }

        // SIMULACIÓN - En producción usarías Twilio, etc.
        log.info("📱 ENVIANDO SMS a {}: Tu código de verificación es: {}", phoneNumber, code);

        System.out.println("\n=========================================");
        System.out.println("🚀 SMS SIMULADO PARA PERÚ");
        System.out.println("Para: " + phoneNumber);
        System.out.println("Código: " + code);
        System.out.println("=========================================\n");
    }
}