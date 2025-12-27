package com.example.sistemabackenddebancos.profile.infrastructure.persistence.jpa.mappers;

import com.example.sistemabackenddebancos.profile.domain.model.aggregates.Profile;
import com.example.sistemabackenddebancos.profile.domain.model.entities.Address;
import com.example.sistemabackenddebancos.profile.domain.model.enumerations.AddressType;
import com.example.sistemabackenddebancos.profile.domain.model.enumerations.DocumentType;
import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.*;
import com.example.sistemabackenddebancos.profile.infrastructure.persistence.jpa.entities.AddressEntity;
import com.example.sistemabackenddebancos.profile.infrastructure.persistence.jpa.entities.ProfileEntity;

import java.util.ArrayList;
import java.util.List;

public class ProfilePersistenceMapper {

    public static Profile toDomain(ProfileEntity e) {
        var fullName = new FullName(e.getGivenNames(), e.getPaternalSurname(), e.getMaternalSurname());
        var phone = new PhoneNumber(e.getPhoneNumber());

        DocumentNumber doc = null;
        if (e.getDocumentType() != null && e.getDocumentNumber() != null) {
            doc = new DocumentNumber(DocumentType.valueOf(e.getDocumentType()), e.getDocumentNumber());
        }

        List<Address> addresses = e.getAddresses().stream().map(a ->
                new Address(
                        new AddressId(a.getId()),
                        AddressType.valueOf(a.getType()),
                        a.getLine1(),
                        a.getLine2(),
                        a.getCity(),
                        a.getRegion(),
                        a.getCountry(),
                        a.getPostalCode(),
                        a.isPrimary()
                )
        ).toList();

        return new Profile(
                new ProfileId(e.getId()),
                new UserId(e.getUserId()),
                fullName,
                phone,
                doc,
                e.getKycStatus(),
                addresses
        );
    }

    public static ProfileEntity toEntity(Profile p) {
        var e = new ProfileEntity();
        e.setId(p.id().value());
        e.setUserId(p.userId().value());

        e.setGivenNames(p.fullName().givenNames());
        e.setPaternalSurname(p.fullName().paternalSurname());
        e.setMaternalSurname(p.fullName().maternalSurname());

        e.setPhoneNumber(p.phoneNumber().value());

        if (p.documentNumber() != null) {
            e.setDocumentType(p.documentNumber().type().name());
            e.setDocumentNumber(p.documentNumber().value());
        } else {
            e.setDocumentType(null);
            e.setDocumentNumber(null);
        }

        e.setKycStatus(p.kycStatus());

        var list = new ArrayList<AddressEntity>();
        for (var a : p.addresses()) {
            var ae = new AddressEntity();
            ae.setId(a.id().value());
            ae.setType(a.type().name());
            ae.setLine1(a.line1());
            ae.setLine2(a.line2());
            ae.setCity(a.city());
            ae.setRegion(a.region());
            ae.setCountry(a.country());
            ae.setPostalCode(a.postalCode());
            ae.setPrimary(a.primary());

            ae.setProfile(e); // relación
            list.add(ae);
        }
        e.setAddresses(list);

        return e;
    }
}
