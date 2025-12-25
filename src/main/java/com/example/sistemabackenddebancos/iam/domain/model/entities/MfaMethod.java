package com.example.sistemabackenddebancos.iam.domain.model.entities;

import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.MfaMethodId;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.MfaType;

import java.util.Objects;

public class MfaMethod {

    private final MfaMethodId id;
    private final MfaType type;
    private final String destination; // para AUTH_APP: etiqueta (ej: "My iPhone")
    private final boolean verified;
    private final String secret; // solo AUTH_APP (nullable)

    public MfaMethod(MfaMethodId id, MfaType type, String destination, boolean verified, String secret) {
        this.id = Objects.requireNonNull(id, "MfaMethod.id cannot be null");
        this.type = Objects.requireNonNull(type, "MfaMethod.type cannot be null");

        String dest = Objects.requireNonNull(destination, "MfaMethod.destination cannot be null").trim();
        if (dest.isEmpty()) throw new IllegalArgumentException("MfaMethod.destination cannot be empty");

        if (type == MfaType.AUTH_APP) {
            if (secret == null || secret.trim().isEmpty())
                throw new IllegalArgumentException("AUTH_APP requires a secret");
        }

        this.destination = dest;
        this.verified = verified;
        this.secret = secret;
    }

    public MfaMethodId id() { return id; }
    public MfaType type() { return type; }
    public String destination() { return destination; }
    public boolean verified() { return verified; }
    public String secret() { return secret; }

    public MfaMethod verify() {
        return new MfaMethod(this.id, this.type, this.destination, true, this.secret);
    }
}