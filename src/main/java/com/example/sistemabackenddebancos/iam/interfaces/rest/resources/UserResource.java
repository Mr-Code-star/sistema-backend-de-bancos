package com.example.sistemabackenddebancos.iam.interfaces.rest.resources;

import com.example.sistemabackenddebancos.iam.domain.model.commands.ChangePasswordCommand;
import com.example.sistemabackenddebancos.iam.domain.model.commands.RegisterMfaMethodCommand;
import com.example.sistemabackenddebancos.iam.domain.model.queries.GetMfaMethodsQuery;
import com.example.sistemabackenddebancos.iam.domain.model.queries.GetUserByEmailQuery;
import com.example.sistemabackenddebancos.iam.domain.model.queries.GetUserByIdQuery;
import com.example.sistemabackenddebancos.iam.domain.model.queries.GetUserStatusQuery;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.Email;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.MfaType;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.UserId;
import com.example.sistemabackenddebancos.iam.domain.services.UserCommandService;
import com.example.sistemabackenddebancos.iam.domain.services.UserQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/iam/users")
public class UserResource {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    public UserResource(UserCommandService userCommandService, UserQueryService userQueryService) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
    }

    // -------- Queries --------

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        var q = new GetUserByIdQuery(new UserId(UUID.fromString(id)));
        return userQueryService.handle(q)
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(Map.of(
                        "id", u.id().value().toString(),
                        "email", u.email().value(),
                        "status", u.status().name(),
                        "mfaEnabled", u.mfaEnabled(),
                        "failedAttempts", u.failedAttempts()
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-email")
    public ResponseEntity<?> getByEmail(@RequestParam String email) {
        var q = new GetUserByEmailQuery(new Email(email));
        return userQueryService.handle(q)
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(Map.of(
                        "id", u.id().value().toString(),
                        "email", u.email().value(),
                        "status", u.status().name()
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<?> getStatus(@PathVariable String id) {
        var q = new GetUserStatusQuery(new UserId(UUID.fromString(id)));
        return userQueryService.handle(q)
                .<ResponseEntity<?>>map(s -> ResponseEntity.ok(Map.of("status", s.name())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/mfa-methods")
    public ResponseEntity<?> getMfaMethods(@PathVariable String id) {
        var q = new GetMfaMethodsQuery(new UserId(UUID.fromString(id)));
        var methods = userQueryService.handle(q).stream().map(m -> Map.of(
                "id", m.id().value().toString(),
                "type", m.type().name(),
                "destination", m.destination(),
                "verified", m.verified()
        )).toList();
        return ResponseEntity.ok(methods);
    }

    // -------- Commands --------

    public record ChangePasswordRequest(String currentPassword, String newPassword) {}

    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable String id, @RequestBody ChangePasswordRequest req) {
        var cmd = new ChangePasswordCommand(
                new UserId(UUID.fromString(id)),
                req.currentPassword(),
                req.newPassword()
        );

        return userCommandService.handle(cmd)
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(Map.of("updated", true)))
                .orElseGet(() -> ResponseEntity.badRequest().body("Could not change password"));
    }

    public record RegisterMfaMethodRequest(String type, String destination) {}

    @PostMapping("/{id}/mfa-methods")
    public ResponseEntity<?> registerMfa(@PathVariable String id, @RequestBody RegisterMfaMethodRequest req) {
        var cmd = new RegisterMfaMethodCommand(
                new UserId(UUID.fromString(id)),
                MfaType.valueOf(req.type()),
                req.destination()
        );

        return userCommandService.handle(cmd)
                .<ResponseEntity<?>>map(u -> ResponseEntity.status(201).body(Map.of("mfaEnabled", u.mfaEnabled())))
                .orElseGet(() -> ResponseEntity.badRequest().body("Could not register mfa method"));
    }
}