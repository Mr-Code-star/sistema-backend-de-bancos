package com.example.sistemabackenddebancos.iam.interfaces.rest.dtos.requests;

public record VerifyMfaRequest(String mfaMethodId, String code) {}
