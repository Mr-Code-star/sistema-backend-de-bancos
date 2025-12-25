package com.example.sistemabackenddebancos.iam.interfaces.rest.resources;

import com.example.sistemabackenddebancos.iam.application.services.MfaAuthAppService;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/iam/auth/mfa/auth-app")
public class MfaAuthAppResource {

    private final MfaAuthAppService mfaAuthAppService;

    public MfaAuthAppResource(MfaAuthAppService mfaAuthAppService) {
        this.mfaAuthAppService = mfaAuthAppService;
    }

    public record SetupRequest(String userId, String deviceLabel) {}
    public record SetupResponse(String methodId, String secret, String qrUri) {}

    @PostMapping("/setup")
    public ResponseEntity<?> setup(@RequestBody SetupRequest req) {
        var userId = new UserId(UUID.fromString(req.userId()));
        var issuer = "BankApp"; // cámbialo por el nombre real de tu app
        var result = mfaAuthAppService.setup(userId, req.deviceLabel(), issuer);
        return ResponseEntity.ok(new SetupResponse(result.methodId(), result.secret(), result.qrUri()));
    }

    public record VerifyRequest(String userId, String methodId, String code) {}
    public record VerifyResponse(boolean verified) {}

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyRequest req) {
        var userId = new UserId(UUID.fromString(req.userId()));
        var methodId = UUID.fromString(req.methodId());
        var result = mfaAuthAppService.verify(userId, methodId, req.code());
        if (!result.verified()) return ResponseEntity.badRequest().body("Invalid MFA code");
        return ResponseEntity.ok(new VerifyResponse(true));
    }
}
