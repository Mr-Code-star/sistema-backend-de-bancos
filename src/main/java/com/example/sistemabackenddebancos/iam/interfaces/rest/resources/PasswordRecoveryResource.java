package com.example.sistemabackenddebancos.iam.interfaces.rest.resources;

import com.example.sistemabackenddebancos.iam.application.security.password.PasswordRecoveryService;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/iam/password-recovery")
public class PasswordRecoveryResource {

    private final PasswordRecoveryService service;

    public PasswordRecoveryResource(PasswordRecoveryService service) {
        this.service = service;
    }

    public record RequestResetBody(String email) {}
    public record ResetBody(String token, String newPassword) {}

    @PostMapping("/request")
    public ResponseEntity<?> request(@RequestBody RequestResetBody body) {
        service.requestReset(new Email(body.email()));
        // Siempre OK para no filtrar usuarios
        return ResponseEntity.ok(Map.of("message", "If the email exists, a reset link has been sent."));
    }

    @PostMapping("/reset")
    public ResponseEntity<?> reset(@RequestBody ResetBody body) {
        boolean ok = service.resetPassword(body.token(), body.newPassword());
        if (!ok) return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired token"));
        return ResponseEntity.ok(Map.of("message", "Password updated"));
    }
}