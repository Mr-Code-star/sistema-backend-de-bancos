package com.example.sistemabackenddebancos.profile.domain.model.aggregates;

import com.example.sistemabackenddebancos.profile.domain.model.entities.Address;
import com.example.sistemabackenddebancos.profile.domain.model.enumerations.*;
import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Profile {

    private static final int MAX_ADDRESSES = 3;

    private final ProfileId id;
    private final UserId userId;

    private final FullName fullName;
    private final PhoneNumber phoneNumber;

    private final DocumentNumber documentNumber; // opcional
    private final KycStatus kycStatus;

    private final List<Address> addresses;

    public Profile(ProfileId id,
                   UserId userId,
                   FullName fullName,
                   PhoneNumber phoneNumber,
                   DocumentNumber documentNumber,
                   KycStatus kycStatus,
                   List<Address> addresses) {

        this.id = Objects.requireNonNull(id, "Profile.id cannot be null");
        this.userId = Objects.requireNonNull(userId, "Profile.userId cannot be null");
        this.fullName = Objects.requireNonNull(fullName, "Profile.fullName cannot be null");
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "Profile.phoneNumber cannot be null");
        this.documentNumber = documentNumber; // nullable OK
        this.kycStatus = Objects.requireNonNull(kycStatus, "Profile.kycStatus cannot be null");

        var list = (addresses == null) ? new ArrayList<Address>() : new ArrayList<>(addresses);
        ensureAddressRules(list);
        this.addresses = Collections.unmodifiableList(list);
    }

    public static Profile createNew(UserId userId, FullName fullName, PhoneNumber phoneNumber, DocumentNumber documentNumber) {
        return new Profile(ProfileId.newId(), userId, fullName, phoneNumber, documentNumber, KycStatus.PENDING, List.of());
    }

    public ProfileId id() { return id; }
    public UserId userId() { return userId; }
    public FullName fullName() { return fullName; }
    public PhoneNumber phoneNumber() { return phoneNumber; }
    public DocumentNumber documentNumber() { return documentNumber; }
    public KycStatus kycStatus() { return kycStatus; }
    public List<Address> addresses() { return addresses; }

    public Profile updateBasicInfo(FullName newFullName, PhoneNumber newPhoneNumber) {
        return new Profile(id, userId, newFullName, newPhoneNumber, documentNumber, kycStatus, addresses);
    }

    public Profile updateDocument(DocumentNumber newDocument) {
        // regla realista: si ya está VERIFIED, bloquear cambio de documento
        if (kycStatus == KycStatus.VERIFIED) {
            throw new IllegalStateException("Cannot change document when KYC is VERIFIED");
        }
        return new Profile(id, userId, fullName, phoneNumber, newDocument, kycStatus, addresses);
    }

    public Profile addAddress(AddressType type,
                              String line1,
                              String line2,
                              String city,
                              String region,
                              String country,
                              String postalCode,
                              boolean makePrimary) {

        var list = new ArrayList<>(addresses);

        if (list.size() >= MAX_ADDRESSES) {
            throw new IllegalStateException("Max addresses reached (" + MAX_ADDRESSES + ")");
        }

        var address = new Address(
                AddressId.newId(),
                type,
                line1, line2, city, region, country, postalCode,
                makePrimary
        );

        list.add(address);

        if (makePrimary) {
            list = forceSinglePrimary(list, address.id());
        } else if (list.stream().noneMatch(Address::primary)) {
            // si no hay primary, el primero se vuelve primary
            list = forceSinglePrimary(list, address.id());
        }

        return new Profile(id, userId, fullName, phoneNumber, documentNumber, kycStatus, list);
    }

    public Profile updateAddress(AddressId addressId,
                                 AddressType type,
                                 String line1,
                                 String line2,
                                 String city,
                                 String region,
                                 String country,
                                 String postalCode) {

        var list = new ArrayList<>(addresses);
        boolean found = false;

        for (int i = 0; i < list.size(); i++) {
            var a = list.get(i);
            if (a.id().equals(addressId)) {
                list.set(i, a.update(type, line1, line2, city, region, country, postalCode));
                found = true;
                break;
            }
        }

        if (!found) throw new IllegalArgumentException("Address not found");

        return new Profile(id, userId, fullName, phoneNumber, documentNumber, kycStatus, list);
    }

    public Profile removeAddress(AddressId addressId) {
        var list = new ArrayList<>(addresses);
        boolean removed = list.removeIf(a -> a.id().equals(addressId));
        if (!removed) throw new IllegalArgumentException("Address not found");

        // si removiste la primary, elige otra
        if (list.stream().noneMatch(Address::primary) && !list.isEmpty()) {
            list = forceSinglePrimary(list, list.get(0).id());
        }

        return new Profile(id, userId, fullName, phoneNumber, documentNumber, kycStatus, list);
    }

    public Profile setPrimaryAddress(AddressId addressId) {
        var list = forceSinglePrimary(new ArrayList<>(addresses), addressId);
        return new Profile(id, userId, fullName, phoneNumber, documentNumber, kycStatus, list);
    }

    public Profile markKycVerified() {
        return new Profile(id, userId, fullName, phoneNumber, documentNumber, KycStatus.VERIFIED, addresses);
    }

    private static void ensureAddressRules(List<Address> list) {
        long primaries = list.stream().filter(Address::primary).count();
        if (primaries > 1) throw new IllegalArgumentException("Only one primary address is allowed");
    }

    private static ArrayList<Address> forceSinglePrimary(List<Address> list, AddressId primaryId) {
        var out = new ArrayList<Address>(list.size());
        boolean exists = list.stream().anyMatch(a -> a.id().equals(primaryId));
        if (!exists) throw new IllegalArgumentException("Address not found for primary");

        for (var a : list) {
            out.add(a.withPrimary(a.id().equals(primaryId)));
        }
        return out;
    }
}
