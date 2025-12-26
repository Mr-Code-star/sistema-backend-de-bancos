package com.example.sistemabackenddebancos.iam.interfaces.rest.resources;


import com.example.sistemabackenddebancos.iam.domain.model.commands.LoginCommand;
import com.example.sistemabackenddebancos.iam.domain.model.commands.RegisterUserCommand;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.Email;
import com.example.sistemabackenddebancos.iam.domain.services.UserCommandService;
import com.example.sistemabackenddebancos.iam.interfaces.rest.resources.dtos.AuthResponse;
import com.example.sistemabackenddebancos.iam.interfaces.rest.resources.dtos.LoginRequest;
import com.example.sistemabackenddebancos.iam.interfaces.rest.resources.dtos.RegisterUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/iam/auth")
public class AuthResource {

    private final UserCommandService userCommandService;

    public AuthResource(UserCommandService userCommandService  ) {
        this.userCommandService = userCommandService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserRequest req) {
        var cmd = new RegisterUserCommand(new Email(req.email()), req.password());
        var created = userCommandService.handle(cmd);

        return created
                .<ResponseEntity<?>>map(u -> ResponseEntity.status(201).body(
                        new AuthResponse(u.id().value().toString(), u.email().value(), u.status().name(), null)
                ))
                .orElseGet(() -> ResponseEntity.badRequest().body("Email already exists"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        var cmd = new LoginCommand(new Email(req.email()), req.password(), req.mfaCode());
        var result = userCommandService.handle(cmd);

        return result
                .<ResponseEntity<?>>map(pair -> {
                    var u = pair.left;
                    var token = pair.right;
                    return ResponseEntity.ok(new AuthResponse(
                            u.id().value().toString(),
                            u.email().value(),
                            u.status().name(),
                            token
                    ));
                })
                .orElseGet(() -> ResponseEntity.status(401).body("Invalid credentials"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String message = "Sesión cerrada exitosamente";

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // Opcional: Podrías registrar el logout en logs
            // logger.info("User logged out with token: {}", token.substring(0, 10) + "...");
            message = "Sesión cerrada. Token invalidado en el cliente.";
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", message,
                "action", "Eliminar el token JWT del almacenamiento del cliente",
                "nextStep", "El usuario puede volver a iniciar sesión cuando desee"
        ));
    }
}