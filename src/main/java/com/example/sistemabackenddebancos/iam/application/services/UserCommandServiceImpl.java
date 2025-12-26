package com.example.sistemabackenddebancos.iam.application.services;

import com.example.sistemabackenddebancos.iam.application.security.hashing.PasswordHasher;
import com.example.sistemabackenddebancos.iam.application.security.mfa.EmailService;
import com.example.sistemabackenddebancos.iam.application.security.mfa.SmsService;
import com.example.sistemabackenddebancos.iam.application.security.tokens.TokenService;
import com.example.sistemabackenddebancos.iam.domain.model.aggregates.User;
import com.example.sistemabackenddebancos.iam.domain.model.commands.ChangePasswordCommand;
import com.example.sistemabackenddebancos.iam.domain.model.commands.LoginCommand;
import com.example.sistemabackenddebancos.iam.domain.model.commands.RegisterMfaMethodCommand;
import com.example.sistemabackenddebancos.iam.domain.model.commands.RegisterUserCommand;
import com.example.sistemabackenddebancos.iam.domain.model.entities.MfaMethod;
import com.example.sistemabackenddebancos.iam.domain.model.enumerations.UserStatus;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.MfaMethodId;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.MfaType;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.PasswordHash;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.UserId;
import com.example.sistemabackenddebancos.iam.domain.repositories.UserRepository;
import com.example.sistemabackenddebancos.iam.domain.services.UserCommandService;
import com.example.sistemabackenddebancos.iam.infrastructure.security.mfa.VerificationCodeService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserCommandServiceImpl implements UserCommandService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Logger log = LoggerFactory.getLogger(UserCommandServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;
    private final SmsService smsService;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;


    public UserCommandServiceImpl(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            TokenService tokenService,
            SmsService smsService,
            EmailService emailService,
            VerificationCodeService verificationCodeService

    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
        this.smsService = smsService;
        this.emailService = emailService;
        this.verificationCodeService = verificationCodeService;

    }


    @Override
    public Optional<ImmutablePair<User, String>> handle(LoginCommand command) {
        var userOpt = userRepository.findByEmail(command.email());
        if (userOpt.isEmpty()) return Optional.empty();

        var user = userOpt.get();

        // Verificar estado - solo bloquear si está BLOCKED
        if (user.status() == UserStatus.BLOCKED) return Optional.empty();

        // Si está PENDING, activarlo automáticamente
        boolean shouldActivate = user.status() == UserStatus.PENDING;

        // Verificar contraseña
        boolean ok = passwordHasher.matches(command.plainPassword(), user.passwordHash().value());
        if (!ok) {
            int newFailed = user.failedAttempts() + 1;
            var newStatus = (newFailed >= MAX_FAILED_ATTEMPTS) ? UserStatus.BLOCKED : user.status();
            var updated = copyUser(user, null, null, newStatus, null, null, newFailed);
            userRepository.save(updated);
            return Optional.empty();
        }

        // Create a final variable for use in lambda
        final User finalUser;

        // Si debe activarse, crear usuario activado
        if (shouldActivate) {
            var activatedUser = copyUser(user, null, null, UserStatus.ACTIVE, null, null, 0);
            finalUser = userRepository.save(activatedUser);
        }
        // Resetear intentos fallidos
        else if (user.failedAttempts() != 0) {
            var updatedUser = copyUser(user, null, null, null, null, null, 0);
            finalUser = userRepository.save(updatedUser);
        } else {
            finalUser = user;
        }

        // ¡¡¡CAMBIOS AQUÍ!!! - VERIFICACIÓN MFA MÁS ESTRICTA
        if (finalUser.mfaEnabled()) {
            // ✅ NUEVA LÓGICA: Si mfaEnabled = true, SIEMPRE requerir código
            if (command.mfaCode() == null || command.mfaCode().trim().isEmpty()) {
                // Se requiere MFA pero no se proporcionó código
                return Optional.empty();
            }

            // Verificar si hay métodos MFA registrados (verificados o no)
            var allMethods = finalUser.mfaMethods();
            if (allMethods.isEmpty()) {
                // Si está habilitado pero no tiene métodos, algo está mal
                // Podemos deshabilitar MFA o rechazar login
                log.warn("Usuario tiene MFA habilitado pero no tiene métodos registrados. User ID: {}", finalUser.id());
                return Optional.empty();
            }

            // Verificar el código contra TODOS los métodos (verificados o no)
            boolean mfaVerified = allMethods.stream()
                    .anyMatch(method -> {
                        if (method.type() == MfaType.SMS) {
                            return verificationCodeService.verifyCode(
                                    finalUser.id().value().toString(),
                                    MfaType.SMS,
                                    method.destination(),
                                    command.mfaCode()
                            );
                        } else if (method.type() == MfaType.EMAIL) {
                            return verificationCodeService.verifyCode(
                                    finalUser.id().value().toString(),
                                    MfaType.EMAIL,
                                    method.destination(),
                                    command.mfaCode()
                            );
                        }
                        return false;
                    });

            if (!mfaVerified) {
                return Optional.empty();
            }

            // ✅ Si el código es correcto, marcar el método como VERIFICADO automáticamente
            // (Esto es opcional, pero buena práctica)
            boolean shouldMarkAsVerified = allMethods.stream()
                    .anyMatch(method -> !method.verified() &&
                            ((method.type() == MfaType.SMS &&
                                    verificationCodeService.verifyCode(finalUser.id().value().toString(), MfaType.SMS, method.destination(), command.mfaCode())) ||
                                    (method.type() == MfaType.EMAIL &&
                                            verificationCodeService.verifyCode(finalUser.id().value().toString(), MfaType.EMAIL, method.destination(), command.mfaCode()))));

            if (shouldMarkAsVerified) {
                var updatedMethods = allMethods.stream()
                        .map(method -> {
                            // Verificar si este método coincide con el código
                            boolean codeMatches = false;
                            if (method.type() == MfaType.SMS) {
                                codeMatches = verificationCodeService.verifyCode(
                                        finalUser.id().value().toString(),
                                        MfaType.SMS,
                                        method.destination(),
                                        command.mfaCode()
                                );
                            } else if (method.type() == MfaType.EMAIL) {
                                codeMatches = verificationCodeService.verifyCode(
                                        finalUser.id().value().toString(),
                                        MfaType.EMAIL,
                                        method.destination(),
                                        command.mfaCode()
                                );
                            }

                            // Si coincide y no está verificado, verificarlo
                            return codeMatches && !method.verified() ? method.verify() : method;
                        })
                        .collect(Collectors.toList());

                var updatedUser = copyUser(finalUser, null, null, null, null, updatedMethods, null);
                userRepository.save(updatedUser);
            }
        }

        var token = tokenService.generate(finalUser);
        return Optional.of(new ImmutablePair<>(finalUser, token));
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

        // Validar destino según tipo
        if (command.type() == MfaType.SMS) {
            if (!smsService.isValidPhoneNumber(command.destination())) {
                throw new IllegalArgumentException("Número peruano inválido. Formato: +519XXXXXXXX");
            }

            // Generar y enviar código de verificación
            String code = verificationCodeService.generateCode(
                    user.id().value().toString(),
                    MfaType.SMS,
                    command.destination()
            );

            smsService.sendVerificationCode(command.destination(), code);

        } else if (command.type() == MfaType.EMAIL) {
            if (!emailService.isValidEmail(command.destination())) {
                throw new IllegalArgumentException("Email inválido");
            }

            // Generar y enviar código de verificación
            String code = verificationCodeService.generateCode(
                    user.id().value().toString(),
                    MfaType.EMAIL,
                    command.destination()
            );

            emailService.sendVerificationCode(command.destination(), code);
        }

        var newMethod = new MfaMethod(
                MfaMethodId.newId(),
                command.type(),
                command.destination(),
                false // No verificado aún
        );

        var newList = new ArrayList<>(user.mfaMethods());
        newList.add(newMethod);

        // ✅ CAMBIAR: Habilitar MFA inmediatamente
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