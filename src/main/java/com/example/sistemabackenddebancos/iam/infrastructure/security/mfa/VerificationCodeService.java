package com.example.sistemabackenddebancos.iam.infrastructure.security.mfa;


import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.MfaType;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationCodeService {

    private final Map<String, CodeInfo> codes = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    // Generar código de 6 dígitos
    public String generateCode(String userId, MfaType type, String destination) {
        int code = 100000 + random.nextInt(900000); // 100000-999999
        String codeStr = String.valueOf(code);

        String key = createKey(userId, type, destination);
        codes.put(key, new CodeInfo(codeStr, LocalDateTime.now()));

        return codeStr;
    }

    // Verificar código
    public boolean verifyCode(String userId, MfaType type, String destination, String code) {
        String key = createKey(userId, type, destination);
        CodeInfo info = codes.get(key);

        if (info == null || !info.code.equals(code)) {
            return false;
        }

        // Código válido por 5 minutos
        boolean valid = info.createdAt.plusMinutes(5).isAfter(LocalDateTime.now());

        if (valid) {
            codes.remove(key); // Usar una sola vez
        }

        return valid;
    }

    private String createKey(String userId, MfaType type, String destination) {
        return userId + ":" + type + ":" + destination;
    }

    // Limpiar códigos expirados
    public void cleanupExpiredCodes() {
        LocalDateTime now = LocalDateTime.now();
        codes.entrySet().removeIf(entry ->
                entry.getValue().createdAt.plusMinutes(10).isBefore(now)
        );
    }

    private static class CodeInfo {
        String code;
        LocalDateTime createdAt;

        CodeInfo(String code, LocalDateTime createdAt) {
            this.code = code;
            this.createdAt = createdAt;
        }
    }
}