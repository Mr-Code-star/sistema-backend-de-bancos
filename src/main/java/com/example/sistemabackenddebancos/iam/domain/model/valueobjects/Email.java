package com.example.sistemabackenddebancos.iam.domain.model.valueobjects;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final String value;

    public Email(String value) {
        String v = Objects.requireNonNull(value, "Email cannot be null").trim().toLowerCase();
        if (v.isEmpty() || !EMAIL_PATTERN.matcher(v).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.value = v;
    }

    public String value(){
        return value;
    }

    public boolean equals (Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        Email email = (Email) o;
        return value.equals(email.value);
    }

    public int hashCode() {
        return value.hashCode();
    }

    public String toString() {
        return value;
    }
}
