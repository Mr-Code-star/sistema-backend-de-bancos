package com.example.sistemabackenddebancos.iam.infrastructure.security.mfa;

import com.example.sistemabackenddebancos.iam.application.security.mfa.SmsService;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class VonageSmsService implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(VonageSmsService.class);

    @Value("${vonage.api.key}")
    private String apiKey;

    @Value("${vonage.api.secret}")
    private String apiSecret;

    @Value("${vonage.sender.id:SistemaBancos}")
    private String senderId;

    private VonageClient client;

    @PostConstruct
    public void init() {
        try {
            log.info("🔍 INICIANDO VONAGE SMS SERVICE...");
            log.info("📱 API Key: {}", apiKey);
            log.info("🔑 API Secret: {}", apiSecret != null ? "***" + apiSecret.substring(Math.max(0, apiSecret.length() - 4)) : "NULL");
            log.info("📞 Sender ID: {}", senderId);

            client = VonageClient.builder()
                    .apiKey(apiKey)
                    .apiSecret(apiSecret)
                    .build();

            log.info("✅ VONAGE CLIENT INICIALIZADO CORRECTAMENTE");

        } catch (Exception e) {
            log.error("❌ ERROR INICIALIZANDO VONAGE: {}", e.getMessage());
            throw new RuntimeException("Fallo inicialización Vonage: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }

        // Vonage acepta formato internacional (+519...)
        boolean isValid = phoneNumber.startsWith("+51") &&
                phoneNumber.length() == 12 &&
                phoneNumber.matches("\\+51[0-9]{9}") &&
                phoneNumber.substring(3).startsWith("9");

        if (!isValid) {
            log.warn("Número inválido para Vonage: {}. Formato requerido: +519XXXXXXXX", phoneNumber);
        }

        return isValid;
    }

    @Override
    public void sendVerificationCode(String phoneNumber, String code) {
        log.info("🚀 ENVIANDO SMS CON VONAGE A PERÚ: {}", phoneNumber);

        if (!isValidPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException(
                    "Número peruano inválido. Formato: +519XXXXXXXX (ej: +51987654321)"
            );
        }

        try {
            // Mensaje claro y conciso
            String messageText = String.format(
                    "Sistema de Bancos\n" +
                            "Tu código de verificación es: %s\n" +
                            "Válido por 5 minutos",
                    code
            );

            log.info("📤 Enviando desde '{}' a {}", senderId, phoneNumber);
            log.info("📝 Mensaje: {}", messageText);

            // Crear y enviar mensaje
            TextMessage message = new TextMessage(
                    senderId,           // Remitente (aparece en el SMS)
                    phoneNumber,        // Destino (+519xxxxxxxx)
                    messageText         // Texto del mensaje
            );

            // ENVÍO REAL
            SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);

            // Verificar respuesta
            if (response.getMessages().size() > 0) {
                com.vonage.client.sms.SmsSubmissionResponseMessage msg = response.getMessages().get(0);

                log.info("📱 Status: {}", msg.getStatus());
                log.info("📱 Message ID: {}", msg.getId());
                log.info("📱 To: {}", msg.getTo());
                log.info("📱 Remaining Balance: {}", msg.getRemainingBalance());

                if (msg.getStatus() == MessageStatus.OK) {
                    log.info("✅✅✅ SMS ENVIADO CON ÉXITO VIA VONAGE ✅✅✅");
                    log.info("🎉 DEBERÍAS RECIBIR EL SMS EN TU CELULAR AHORA!");
                } else {
                    log.error("❌ ERROR VONAGE: {}", msg.getErrorText());
                    throw new RuntimeException("Error Vonage: " + msg.getErrorText());
                }
            } else {
                log.error("❌ NO HAY RESPUESTA DE VONAGE");
                throw new RuntimeException("No response from Vonage");
            }

        } catch (Exception e) {
            log.error("❌❌❌ ERROR ENVIANDO SMS VONAGE: {}", e.getMessage(), e);
            throw new RuntimeException("Error enviando SMS con Vonage: " + e.getMessage(), e);
        }
    }

    // Método de prueba rápido
    public String sendTestSms(String peruPhoneNumber) {
        String testCode = String.valueOf(100000 + (int)(Math.random() * 900000));
        sendVerificationCode(peruPhoneNumber, testCode);
        return "Test SMS enviado a " + peruPhoneNumber + " con código: " + testCode;
    }
}