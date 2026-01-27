package com.example.sistemabackenddebancos.iam.infrastructure.persistence.jpa.adapters;

import com.example.sistemabackenddebancos.iam.domain.model.aggregates.PasswordResetToken;
import com.example.sistemabackenddebancos.iam.domain.model.enumerations.PasswordResetTokenStatus;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.UserId;
import com.example.sistemabackenddebancos.iam.domain.repositories.PasswordResetTokenRepository;
import com.example.sistemabackenddebancos.iam.infrastructure.persistence.jpa.entities.PasswordResetTokenEntity;
import com.example.sistemabackenddebancos.iam.infrastructure.persistence.jpa.repositories.SpringDataPasswordResetTokenJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaPasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepository {

    private final SpringDataPasswordResetTokenJpaRepository jpa;

    public JpaPasswordResetTokenRepositoryAdapter(SpringDataPasswordResetTokenJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<PasswordResetToken> findActiveByTokenHash(String tokenHash) {
        return jpa.findByTokenHashAndStatus(tokenHash, PasswordResetTokenStatus.ACTIVE)
                .map(this::toDomain);
    }

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        var saved = jpa.save(toEntity(token));
        return toDomain(saved);
    }

    private PasswordResetToken toDomain(PasswordResetTokenEntity e) {
        return new PasswordResetToken(
                e.getId(),
                new UserId(e.getUserId()),
                e.getTokenHash(),
                e.getExpiresAt(),
                e.getStatus(),
                e.getUsedAt()
        );
    }

    private PasswordResetTokenEntity toEntity(PasswordResetToken t) {
        var e = new PasswordResetTokenEntity();
        e.setId(t.id());
        e.setUserId(t.userId().value());
        e.setTokenHash(t.tokenHash());
        e.setExpiresAt(t.expiresAt());
        e.setStatus(t.status());
        e.setUsedAt(t.usedAt());
        return e;
    }
}