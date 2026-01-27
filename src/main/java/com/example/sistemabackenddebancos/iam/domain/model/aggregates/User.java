package com.example.sistemabackenddebancos.iam.domain.model.aggregates;

import com.example.sistemabackenddebancos.iam.domain.model.enumerations.UserStatus;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.Email;
import com.example.sistemabackenddebancos.iam.domain.model.entities.MfaMethod;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.PasswordHash;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.UserId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class User {

    private final UserId id;
    private final Email email;
    private final PasswordHash passwordHash;

    private final UserStatus status;
    private final boolean mfaEnabled;

    private final List<MfaMethod> mfaMethods;
    private final int failedAttempts;

    public User(
            UserId id,
            Email email,
            PasswordHash passwordHash,
            UserStatus status,
            boolean mfaEnabled,
            List<MfaMethod> mfaMethods,
            int failedAttempts
    ) {
        this.id = Objects.requireNonNull(id, "User.id cannot be null");
        this.email = Objects.requireNonNull(email, "User.email cannot be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "User.passwordHash cannot be null");
        this.status = Objects.requireNonNull(status, "User.status cannot be null");

        if (failedAttempts < 0) throw new IllegalArgumentException("failedAttempts cannot be negative");

        this.mfaEnabled = mfaEnabled;
        this.failedAttempts = failedAttempts;

        // defensivo: copia inmutable
        List<MfaMethod> safe = (mfaMethods == null) ? new ArrayList<>() : new ArrayList<>(mfaMethods);
        this.mfaMethods = Collections.unmodifiableList(safe);
    }

    public UserId id() { return id; }
    public Email email() { return email; }
    public PasswordHash passwordHash() { return passwordHash; }
    public UserStatus status() { return status; }
    public boolean mfaEnabled() { return mfaEnabled; }
    public List<MfaMethod> mfaMethods() { return mfaMethods; }
    public int failedAttempts() { return failedAttempts; }

    public User withPasswordHash(PasswordHash newPasswordHash) {
        return new User(
                this.id(),
                this.email(),
                newPasswordHash,
                this.status(),
                this.mfaEnabled(),
                this.mfaMethods(),
                0
        );
    }
}