package com.example.sistemabackenddebancos.iam.interfaces.rest.resources;


import com.example.sistemabackenddebancos.iam.domain.model.commands.LoginCommand;
import com.example.sistemabackenddebancos.iam.domain.model.commands.RegisterUserCommand;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.Email;
import com.example.sistemabackenddebancos.iam.domain.services.UserCommandService;
import com.example.sistemabackenddebancos.iam.interfaces.rest.dtos.responses.AuthResponse;
import com.example.sistemabackenddebancos.iam.interfaces.rest.dtos.requests.LoginRequest;
import com.example.sistemabackenddebancos.iam.interfaces.rest.dtos.requests.RegisterUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}