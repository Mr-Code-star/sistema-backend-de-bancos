package com.example.sistemabackenddebancos.iam.application.services;

import com.example.sistemabackenddebancos.iam.application.security.hashing.PasswordHasher;
import com.example.sistemabackenddebancos.iam.application.security.password.PasswordRecoveryService;
import com.example.sistemabackenddebancos.iam.application.email.JavaMailEmailService;
import com.example.sistemabackenddebancos.iam.domain.model.aggregates.PasswordResetToken;
import com.example.sistemabackenddebancos.iam.domain.model.enumerations.PasswordResetTokenStatus;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.Email;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.PasswordHash;
import com.example.sistemabackenddebancos.iam.domain.repositories.PasswordResetTokenRepository;
import com.example.sistemabackenddebancos.iam.domain.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordHasher passwordHasher;
    private final JavaMailEmailService emailService;

    private final String resetBaseUrl;
    private final int ttlMinutes;

    public PasswordRecoveryServiceImpl(UserRepository userRepository,
                                       PasswordResetTokenRepository tokenRepository,
                                       PasswordHasher passwordHasher,
                                       JavaMailEmailService emailService,
                                       @Value("${app.reset-password.base-url:http://localhost:3000/reset-password}") String resetBaseUrl,
                                       @Value("${app.reset-password.ttl-minutes:15}") int ttlMinutes) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordHasher = passwordHasher;
        this.emailService = emailService;
        this.resetBaseUrl = resetBaseUrl;
        this.ttlMinutes = ttlMinutes;
    }

    @Override
    public void requestReset(Email email) {
        // IMPORTANT: nunca reveles si el email existe o no
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return; // responderemos OK igual desde el controller
        }

        var user = userOpt.get();

        String rawToken = UUID.randomUUID().toString() + "-" + UUID.randomUUID();
        String tokenHash = sha256Hex(rawToken);

        Instant expiresAt = Instant.now().plus(ttlMinutes, ChronoUnit.MINUTES);

        var prt = new PasswordResetToken(
                UUID.randomUUID(),
                user.id(),
                tokenHash,
                expiresAt,
                PasswordResetTokenStatus.ACTIVE,
                null
        );
        tokenRepository.save(prt);

        String link = resetBaseUrl + "?token=" + rawToken;

        String subject = "Password reset";
        String body = """
                We received a request to reset your password.

                Use this link to reset it (expires in %d minutes):
                %s

                If you did not request this, ignore this email.
                """.formatted(ttlMinutes, link);

        emailService.send(email.value(), subject, body);
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        String tokenHash = sha256Hex(token);

        var prtOpt = tokenRepository.findActiveByTokenHash(tokenHash);
        if (prtOpt.isEmpty()) return false;

        var prt = prtOpt.get();

        if (prt.isExpired(Instant.now())) {
            tokenRepository.save(prt.markExpired());
            return false;
        }

        var userOpt = userRepository.findById(prt.userId());
        if (userOpt.isEmpty()) return false;

        var user = userOpt.get();

        // Cambiar password (depende de tu aggregate User; si tienes método, úsalo.
        var hashed  = passwordHasher.hash(newPassword);

        var updatedUser = user.withPasswordHash(new PasswordHash(hashed));

        userRepository.save(updatedUser);

        tokenRepository.save(prt.markUsed(Instant.now()));
        return true;
    }

    private String sha256Hex(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }
}