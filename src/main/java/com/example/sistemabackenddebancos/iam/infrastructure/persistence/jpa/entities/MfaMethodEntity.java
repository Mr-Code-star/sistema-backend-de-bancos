package com.example.sistemabackenddebancos.iam.infrastructure.persistence.jpa.entities;

import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.MfaType;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "mfa_methods")
public class MfaMethodEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MfaType type;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private boolean verified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "secret")
    private String secret;

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }


    public MfaMethodEntity() {}

    // getters/setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public MfaType getType() { return type; }
    public void setType(MfaType type) { this.type = type; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }
}
