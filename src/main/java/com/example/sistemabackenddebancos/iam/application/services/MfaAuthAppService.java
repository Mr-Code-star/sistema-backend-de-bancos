package com.example.sistemabackenddebancos.iam.application.services;

import com.example.sistemabackenddebancos.iam.application.security.mfa.TotpService;
import com.example.sistemabackenddebancos.iam.domain.model.aggregates.User;
import com.example.sistemabackenddebancos.iam.domain.model.entities.MfaMethod;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.MfaMethodId;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.MfaType;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.UserId;
import com.example.sistemabackenddebancos.iam.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class MfaAuthAppService {

    public record SetupResult(String methodId, String secret, String qrUri) {}
    public record VerifyResult(boolean verified) {}

    private final UserRepository userRepository;
    private final TotpService totpService;

    public MfaAuthAppService(UserRepository userRepository, TotpService totpService) {
        this.userRepository = userRepository;
        this.totpService = totpService;
    }

    public SetupResult setup(UserId userId, String deviceLabel, String issuer) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // ✅ Genera secret sí o sí
        String secret = totpService.generateSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("TOTP secret generator returned empty secret");
        }

        var methodId = MfaMethodId.newId();

        // ✅ AUTH_APP requiere secret, acá NUNCA será null
        var newMethod = new MfaMethod(
                methodId,
                MfaType.AUTH_APP,
                deviceLabel,
                false,
                secret
        );

        var newList = new ArrayList<>(user.mfaMethods());
        newList.add(newMethod);

        var updated = new User(
                user.id(),
                user.email(),
                user.passwordHash(),
                user.status(),
                true,
                newList,
                user.failedAttempts()
        );

        userRepository.save(updated);

        String qrUri = totpService.buildQrUri(issuer, user.email().value(), secret);
        return new SetupResult(methodId.value().toString(), secret, qrUri);
    }

    public VerifyResult verify(UserId userId, UUID methodId, String code) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var list = new ArrayList<>(user.mfaMethods());
        boolean ok = false;

        for (int i = 0; i < list.size(); i++) {
            var m = list.get(i);

            if (m.id().value().equals(methodId) && m.type() == MfaType.AUTH_APP) {
                if (!m.verified() && totpService.verifyCode(m.secret(), code)) {
                    list.set(i, m.verify());
                    ok = true;
                }
                break;
            }
        }

        if (!ok) return new VerifyResult(false);

        var updated = new User(
                user.id(),
                user.email(),
                user.passwordHash(),
                user.status(),
                true,
                list,
                user.failedAttempts()
        );
        userRepository.save(updated);

        return new VerifyResult(true);
    }
}
