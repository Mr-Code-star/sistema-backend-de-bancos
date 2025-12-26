package com.example.sistemabackenddebancos.iam.interfaces.rest.resources.dtos;

public record VerifyMfaRequest(String mfaMethodId, String code) {}
