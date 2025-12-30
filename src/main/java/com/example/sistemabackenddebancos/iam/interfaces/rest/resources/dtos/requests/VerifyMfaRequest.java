package com.example.sistemabackenddebancos.iam.interfaces.rest.resources.dtos.requests;

public record VerifyMfaRequest(String mfaMethodId, String code) {}
