package com.example.sistemabackenddebancos.iam.application.security.password;

import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.Email;

public interface PasswordRecoveryService {
    void requestReset(Email email);
    boolean resetPassword(String token, String newPassword);
}