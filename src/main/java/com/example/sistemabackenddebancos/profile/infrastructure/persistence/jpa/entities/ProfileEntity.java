package com.example.sistemabackenddebancos.profile.infrastructure.persistence.jpa.entities;

import com.example.sistemabackenddebancos.profile.domain.model.enumerations.KycStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "profiles")
public class ProfileEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "given_names", nullable = false)
    private String givenNames;

    @Column(name = "paternal_surname", nullable = false)
    private String paternalSurname;

    @Column(name = "maternal_surname", nullable = false)
    private String maternalSurname;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    // Documento (opcional)
    @Column(name = "document_type")
    private String documentType;

    @Column(name = "document_number")
    private String documentNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", nullable = false)
    private KycStatus kycStatus;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<AddressEntity> addresses = new ArrayList<>();

    @Column(name = "notification_preferences_json", nullable = false, columnDefinition = "json")
    private String notificationPreferencesJson;

    public ProfileEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getGivenNames() { return givenNames; }
    public void setGivenNames(String givenNames) { this.givenNames = givenNames; }

    public String getPaternalSurname() { return paternalSurname; }
    public void setPaternalSurname(String paternalSurname) { this.paternalSurname = paternalSurname; }

    public String getMaternalSurname() { return maternalSurname; }
    public void setMaternalSurname(String maternalSurname) { this.maternalSurname = maternalSurname; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public KycStatus getKycStatus() { return kycStatus; }
    public void setKycStatus(KycStatus kycStatus) { this.kycStatus = kycStatus; }

    public List<AddressEntity> getAddresses() { return addresses; }
    public void setAddresses(List<AddressEntity> addresses) { this.addresses = addresses; }

    public String getNotificationPreferencesJson() { return notificationPreferencesJson; }
    public void setNotificationPreferencesJson(String notificationPreferencesJson) { this.notificationPreferencesJson = notificationPreferencesJson; }
}