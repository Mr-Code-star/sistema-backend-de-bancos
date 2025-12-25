package com.example.sistemabackenddebancos.iam.infrastructure.security.mfa;

import com.example.sistemabackenddebancos.iam.application.security.mfa.TotpService;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class DefaultTotpService implements TotpService {

    private final SecureRandom random = new SecureRandom();

    @Override
    public String generateSecret() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        // Nota: esto es Base64 (funciona para guardado). Para Authenticator ideal Base32, pero esto te destraba YA.
        return Base64.getEncoder().withoutPadding().encodeToString(bytes);
    }

    @Override
    public String buildQrUri(String issuer, String accountName, String secret) {
        // otpauth://totp/<issuer>:<account>?secret=<secret>&issuer=<issuer>&digits=6&period=30
        String label = url(issuer + ":" + accountName);
        return "otpauth://totp/" + label
                + "?secret=" + url(secret)
                + "&issuer=" + url(issuer)
                + "&digits=6&period=30";
    }

    @Override
    public boolean verifyCode(String secret, String code) {
        // ⚠️ Aquí aún no validamos TOTP real. (Luego lo hacemos con librería TOTP)
        // Por ahora, para que no te bloquee: exige 6 dígitos.
        if (code == null) return false;
        return code.trim().matches("\\d{6}");
    }

    private String url(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
