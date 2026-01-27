package com.example.sistemabackenddebancos.admin.interfaces.rest.resources;

import com.example.sistemabackenddebancos.accounts.domain.model.commands.CloseAccountCommand;
import com.example.sistemabackenddebancos.accounts.domain.model.commands.FreezeAccountCommand;
import com.example.sistemabackenddebancos.accounts.domain.model.commands.UnfreezeAccountCommand;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountId;
import com.example.sistemabackenddebancos.accounts.domain.services.AccountCommandService;
import com.example.sistemabackenddebancos.admin.application.services.AdminAuditService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/accounts")
@SecurityRequirement(name = "bearerAuth")
public class AdminAccountResource {

    private final AccountCommandService accountCommandService;
    private final AdminAuditService audit;

    public AdminAccountResource(AccountCommandService accountCommandService, AdminAuditService audit) {
        this.accountCommandService = accountCommandService;
        this.audit = audit;
    }

    private UUID adminUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(auth.getPrincipal().toString());
    }

    public record ReasonRequest(String reason) {}

    @PutMapping("/{accountId}/freeze")
    public ResponseEntity<?> freeze(@PathVariable String accountId, @RequestBody(required = false) ReasonRequest req) {
        var id = new AccountId(UUID.fromString(accountId));
        var result = accountCommandService.handle(new FreezeAccountCommand(id));

        if (result.isEmpty()) return ResponseEntity.notFound().build();

        audit.log(adminUserId(), "FREEZE_ACCOUNT", "ACCOUNT", id.value(), req != null ? req.reason() : null);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}/unfreeze")
    public ResponseEntity<?> unfreeze(@PathVariable String accountId, @RequestBody(required = false) ReasonRequest req) {
        var id = new AccountId(UUID.fromString(accountId));
        var result = accountCommandService.handle(new UnfreezeAccountCommand(id));

        if (result.isEmpty()) return ResponseEntity.notFound().build();

        audit.log(adminUserId(), "UNFREEZE_ACCOUNT", "ACCOUNT", id.value(), req != null ? req.reason() : null);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}/close")
    public ResponseEntity<?> close(@PathVariable String accountId, @RequestBody(required = false) ReasonRequest req) {
        var id = new AccountId(UUID.fromString(accountId));
        var result = accountCommandService.handle(new CloseAccountCommand(id));

        if (result.isEmpty()) return ResponseEntity.notFound().build();

        audit.log(adminUserId(), "CLOSE_ACCOUNT", "ACCOUNT", id.value(), req != null ? req.reason() : null);
        return ResponseEntity.ok().build();
    }
}