package com.example.sistemabackenddebancos.profile.domain.model.entities;

import com.example.sistemabackenddebancos.profile.domain.model.enumerations.*;
import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.*;

import java.util.Objects;

public class Address {

    private final AddressId id;
    private final AddressType type;

    private final String line1;
    private final String line2; // opcional
    private final String city;
    private final String region;
    private final String country;
    private final String postalCode; // opcional

    private final boolean primary;

    public Address(AddressId id,
                   AddressType type,
                   String line1,
                   String line2,
                   String city,
                   String region,
                   String country,
                   String postalCode,
                   boolean primary) {

        this.id = Objects.requireNonNull(id, "Address.id cannot be null");
        this.type = Objects.requireNonNull(type, "Address.type cannot be null");

        this.line1 = normalizeRequired(line1, "line1");
        this.line2 = normalizeOptional(line2);
        this.city = normalizeRequired(city, "city");
        this.region = normalizeRequired(region, "region");
        this.country = normalizeRequired(country, "country");
        this.postalCode = normalizeOptional(postalCode);

        this.primary = primary;
    }

    public AddressId id() { return id; }
    public AddressType type() { return type; }
    public String line1() { return line1; }
    public String line2() { return line2; }
    public String city() { return city; }
    public String region() { return region; }
    public String country() { return country; }
    public String postalCode() { return postalCode; }
    public boolean primary() { return primary; }

    public Address withPrimary(boolean isPrimary) {
        return new Address(id, type, line1, line2, city, region, country, postalCode, isPrimary);
    }

    public Address update(AddressType type,
                          String line1,
                          String line2,
                          String city,
                          String region,
                          String country,
                          String postalCode) {
        return new Address(
                this.id,
                Objects.requireNonNull(type, "Address.type cannot be null"),
                line1,
                line2,
                city,
                region,
                country,
                postalCode,
                this.primary
        );
    }

    private static String normalizeRequired(String v, String field) {
        Objects.requireNonNull(v, field + " cannot be null");
        var s = v.trim();
        if (s.isEmpty()) throw new IllegalArgumentException(field + " cannot be empty");
        return s;
    }

    private static String normalizeOptional(String v) {
        if (v == null) return null;
        var s = v.trim();
        return s.isEmpty() ? null : s;
    }
}