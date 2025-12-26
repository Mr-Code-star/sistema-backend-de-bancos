package com.example.sistemabackenddebancos.iam.application.security.mfa;

public interface SmsService {
    void sendVerificationCode(String phoneNumber, String code);
    boolean isValidPhoneNumber(String phoneNumber);
}