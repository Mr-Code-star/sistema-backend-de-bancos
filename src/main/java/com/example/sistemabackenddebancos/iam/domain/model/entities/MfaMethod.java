package com.example.sistemabackenddebancos.iam.domain.model.entities;

import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.MfaMethodId;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.MfaType;

import java.util.Objects;

public class MfaMethod {

    private final MfaMethodId id;
    private final MfaType type;
    private final String destination;
    private final boolean verified;

    public MfaMethod(MfaMethodId id, MfaType type, String destination, boolean verified) {
        this.id = Objects.requireNonNull(id, "MfaMethod.id cannot be null");
        this.type = Objects.requireNonNull(type, "MfaMethod.type cannot be null");

        String dest = Objects.requireNonNull(destination, "MfaMethod.destination cannot be null").trim();
        if (dest.isEmpty()) throw new IllegalArgumentException("MfaMethod.destination cannot be empty");

        this.destination = dest;
        this.verified = verified;
    }

    public MfaMethodId id() { return id; }
    public MfaType type() { return type; }
    public String destination() { return destination; }
    public boolean verified() { return verified; }
}