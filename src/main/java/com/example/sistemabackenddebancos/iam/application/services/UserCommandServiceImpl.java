package com.example.sistemabackenddebancos.iam.application.services;

import com.example.sistemabackenddebancos.iam.application.security.hashing.PasswordHasher;
import com.example.sistemabackenddebancos.iam.application.security.tokens.TokenService;
import com.example.sistemabackenddebancos.iam.domain.model.aggregates.User;
import com.example.sistemabackenddebancos.iam.domain.model.commands.ChangePasswordCommand;
import com.example.sistemabackenddebancos.iam.domain.model.commands.LoginCommand;
import com.example.sistemabackenddebancos.iam.domain.model.commands.RegisterMfaMethodCommand;
import com.example.sistemabackenddebancos.iam.domain.model.commands.RegisterUserCommand;
import com.example.sistemabackenddebancos.iam.domain.model.entities.MfaMethod;
import com.example.sistemabackenddebancos.iam.domain.model.enumerations.UserStatus;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.MfaMethodId;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.PasswordHash;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.UserId;
import com.example.sistemabackenddebancos.iam.domain.repositories.UserRepository;
import com.example.sistemabackenddebancos.iam.domain.services.UserCommandService;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserCommandServiceImpl implements UserCommandService {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;

    public UserCommandServiceImpl(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
    }

    @Override
    public Optional<ImmutablePair<User, String>> handle(LoginCommand command) {
        var userOpt = userRepository.findByEmail(command.email());
        if (userOpt.isEmpty()) return Optional.empty();

        var user = userOpt.get();

        // reglas mínimas
        if (user.status() == UserStatus.BLOCKED) return Optional.empty();
        if (user.status() != UserStatus.ACTIVE) return Optional.empty();

        boolean ok = passwordHasher.matches(command.plainPassword(), user.passwordHash().value());
        if (!ok) {
            int newFailed = user.failedAttempts() + 1;
            var newStatus = (newFailed >= MAX_FAILED_ATTEMPTS) ? UserStatus.BLOCKED : user.status();
            var updated = copyUser(user, null, null, newStatus, null, null, newFailed);
            userRepository.save(updated);
            return Optional.empty();
        }

        // si está ok, resetea intentos
        if (user.failedAttempts() != 0) {
            user = copyUser(user, null, null, null, null, null, 0);
            userRepository.save(user);
        }

        // MFA (placeholder): si está habilitado, exigir mfaCode no vacío
        if (user.mfaEnabled()) {
            var code = command.mfaCode();
            if (code == null || code.trim().isEmpty()) return Optional.empty();
        }

        var token = tokenService.generate(user);
        return Optional.of(new ImmutablePair<>(user, token));
    }

    @Override
    public Optional<User> handle(RegisterUserCommand command) {
        if (userRepository.existsByEmail(command.email())) return Optional.empty();

        var hashed = passwordHasher.hash(command.plainPassword());
        var user = new User(
                UserId.newId(),
                command.email(),
                new PasswordHash(hashed),
                UserStatus.PENDING,          // luego puedes activar por confirmación
                false,
                List.of(),
                0
        );

        return Optional.of(userRepository.save(user));
    }

    @Override
    public Optional<User> handle(ChangePasswordCommand command) {
        var userOpt = userRepository.findById(command.userId());
        if (userOpt.isEmpty()) return Optional.empty();

        var user = userOpt.get();
        if (user.status() == UserStatus.BLOCKED) return Optional.empty();

        boolean ok = passwordHasher.matches(command.currentPlainPassword(), user.passwordHash().value());
        if (!ok) return Optional.empty();

        var newHashed = passwordHasher.hash(command.newPlainPassword());
        var updated = copyUser(user, null, new PasswordHash(newHashed), null, null, null, null);
        return Optional.of(userRepository.save(updated));
    }

    @Override
    public Optional<User> handle(RegisterMfaMethodCommand command) {
        var userOpt = userRepository.findById(command.userId());
        if (userOpt.isEmpty()) return Optional.empty();

        var user = userOpt.get();

        var newMethod = new MfaMethod(
                MfaMethodId.newId(),
                command.type(),
                command.destination(),
                false
        );

        var newList = new ArrayList<>(user.mfaMethods());
        newList.add(newMethod);

        var updated = copyUser(user, null, null, null, true, newList, null);
        return Optional.of(userRepository.save(updated));
    }

    private User copyUser(
            User base,
            UserId id,
            PasswordHash passwordHash,
            UserStatus status,
            Boolean mfaEnabled,
            List<MfaMethod> mfaMethods,
            Integer failedAttempts
    ) {
        return new User(
                id != null ? id : base.id(),
                base.email(), // email no lo cambiamos acá
                passwordHash != null ? passwordHash : base.passwordHash(),
                status != null ? status : base.status(),
                mfaEnabled != null ? mfaEnabled : base.mfaEnabled(),
                mfaMethods != null ? mfaMethods : base.mfaMethods(),
                failedAttempts != null ? failedAttempts : base.failedAttempts()
        );
    }
}