package com.example.sistemabackenddebancos.iam.domain.repositories;

import com.example.sistemabackenddebancos.iam.domain.model.aggregates.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository {
    Optional<PasswordResetToken> findActiveByTokenHash(String tokenHash);
    PasswordResetToken save(PasswordResetToken token);
}