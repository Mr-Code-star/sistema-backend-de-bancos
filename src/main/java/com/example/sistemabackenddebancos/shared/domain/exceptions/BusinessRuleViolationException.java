package com.example.sistemabackenddebancos.shared.domain.exceptions;

public class BusinessRuleViolationException extends DomainException {
    public BusinessRuleViolationException(String message) { super(message); }
}