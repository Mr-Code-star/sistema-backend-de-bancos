package com.example.sistemabackenddebancos.iam.application.security.mfa;

public interface TotpService {
    String generateSecret();
    String buildQrUri(String issuer, String accountName, String secret);
    boolean verifyCode(String secret, String code);
}