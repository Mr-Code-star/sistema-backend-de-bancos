package com.example.sistemabackenddebancos.iam.infrastructure.security.mfa;

import com.example.sistemabackenddebancos.iam.application.security.mfa.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;

@Service
public class RealEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(RealEmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.email.sender:no-reply@sistemabancos.com}")
    private String fromEmail;

    @Value("${app.email.sender.name:Sistema de Bancos}")
    private String senderName;

    public RealEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public boolean isValidEmail(String email) {
        return email != null &&
                email.contains("@") &&
                email.contains(".") &&
                email.length() > 5 &&
                email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    @Override
    public void sendVerificationCode(String email, String code) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Email inválido: " + email);
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, senderName);
            helper.setTo(email);
            helper.setSubject("🔐 Código de Verificación - Sistema de Bancos");

            // Email con HTML profesional
            String htmlContent = buildEmailHtml(code);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("✅ EMAIL REAL enviado a: {}", email);
            log.info("📧 Código enviado: {}", code);

        } catch (MessagingException e) {
            log.error("❌ Error al enviar email a {}: {}", email, e.getMessage());
            throw new RuntimeException("No se pudo enviar el email de verificación", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildEmailHtml(String code) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; }
                    .header { text-align: center; color: #2c3e50; }
                    .code { font-size: 32px; font-weight: bold; color: #e74c3c; text-align: center; margin: 30px 0; }
                    .warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 10px; margin: 20px 0; }
                    .footer { margin-top: 30px; text-align: center; color: #7f8c8d; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 Sistema de Bancos</h1>
                        <h2>Código de Verificación</h2>
                    </div>
                    
                    <p>Hola,</p>
                    <p>Has solicitado un código de verificación para tu cuenta.</p>
                    
                    <div class="code">
                        """ + code + """
                    </div>
                    
                    <div class="warning">
                        <p><strong>⚠️ Importante:</strong></p>
                        <p>• Este código es válido por <strong>5 minutos</strong></p>
                        <p>• No compartas este código con nadie</p>
                        <p>• Si no solicitaste este código, ignora este email</p>
                    </div>
                    
                    <p>Saludos,<br>El equipo de Sistema de Bancos</p>
                    
                    <div class="footer">
                        <p>© 2024 Sistema de Bancos. Todos los derechos reservados.</p>
                        <p>Este es un mensaje automático, por favor no respondas a este email.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
}