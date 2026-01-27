package com.example.sistemabackenddebancos.accounts.interfaces.rest.resources;

import com.example.sistemabackenddebancos.accounts.domain.model.aggregates.BankAccount;
import com.example.sistemabackenddebancos.accounts.domain.model.commands.*;
import com.example.sistemabackenddebancos.accounts.domain.model.enumerations.AccountType;
import com.example.sistemabackenddebancos.shared.domain.model.enumerations.Currency;
import com.example.sistemabackenddebancos.accounts.domain.model.queries.GetAccountByIdQuery;
import com.example.sistemabackenddebancos.accounts.domain.model.queries.GetAccountByNumberQuery;
import com.example.sistemabackenddebancos.accounts.domain.model.queries.GetAccountsByOwnerIdQuery;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountId;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountNumber;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.OwnerId;
import com.example.sistemabackenddebancos.accounts.domain.services.AccountCommandService;
import com.example.sistemabackenddebancos.accounts.domain.services.AccountQueryService;
import com.example.sistemabackenddebancos.accounts.interfaces.rest.dtos.requests.AmountRequest;
import com.example.sistemabackenddebancos.accounts.interfaces.rest.dtos.requests.OpenAccountRequest;
import com.example.sistemabackenddebancos.accounts.interfaces.rest.dtos.responses.AccountResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@SecurityRequirement(name = "bearerAuth")
public class AccountResource {

    private final AccountCommandService commandService;
    private final AccountQueryService queryService;

    public AccountResource(AccountCommandService commandService, AccountQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    // ✅ helper: userId del JWT (principal = subject)
    private UUID currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) throw new IllegalStateException("Unauthenticated");
        return UUID.fromString(auth.getPrincipal().toString());
    }

    private boolean isOwner(BankAccount a) {
        return a.ownerId().value().equals(currentUserId());
    }

    // -------- OPEN ACCOUNT (ownerId desde JWT) --------
    @PostMapping
    public ResponseEntity<?> open(@RequestBody OpenAccountRequest req) {
        var cmd = new OpenAccountCommand(
                new OwnerId(currentUserId()),
                AccountType.valueOf(req.type()),
                Currency.valueOf(req.currency())
        );

        return commandService.handle(cmd)
                .<ResponseEntity<?>>map(a -> ResponseEntity.status(201).body(toResponse(a)))
                .orElseGet(() -> ResponseEntity.badRequest().body("Could not open account"));
    }

    // -------- GET MY ACCOUNTS (sin ownerId por URL) --------
    @GetMapping("/my")
    public ResponseEntity<?> getMyAccounts() {
        var q = new GetAccountsByOwnerIdQuery(new OwnerId(currentUserId()));
        var list = queryService.handle(q).stream().map(this::toResponse).toList();
        return ResponseEntity.ok(list);
    }

    // -------- GET BY ID (valida ownership) --------
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getById(@PathVariable String accountId) {
        var q = new GetAccountByIdQuery(new AccountId(UUID.fromString(accountId)));

        var opt = queryService.handle(q);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        var account = opt.get();
        if (!isOwner(account)) return ResponseEntity.status(403).body("Forbidden");

        return ResponseEntity.ok(toResponse(account));
    }

    // -------- GET BY NUMBER (valida ownership) --------
    @GetMapping("/by-number/{accountNumber}")
    public ResponseEntity<?> getByNumber(@PathVariable String accountNumber) {
        var q = new GetAccountByNumberQuery(new AccountNumber(accountNumber));

        var opt = queryService.handle(q);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        var account = opt.get();
        if (!isOwner(account)) return ResponseEntity.status(403).body("Forbidden");

        return ResponseEntity.ok(toResponse(account));
    }

    // -------- DEPOSIT (valida ownership) --------
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<?> deposit(@PathVariable String accountId, @RequestBody AmountRequest req) {
        var id = new AccountId(UUID.fromString(accountId));

        // ownership check antes de operar
        var accOpt = queryService.handle(new GetAccountByIdQuery(id));
        if (accOpt.isEmpty()) return ResponseEntity.notFound().build();
        if (!isOwner(accOpt.get())) return ResponseEntity.status(403).body("Forbidden");

        try {
            var cmd = new DepositCommand(id, req.amount());
            return commandService.handle(cmd)
                    .<ResponseEntity<?>>map(a -> ResponseEntity.ok(toResponse(a)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // -------- WITHDRAW (valida ownership) --------
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable String accountId, @RequestBody AmountRequest req) {
        var id = new AccountId(UUID.fromString(accountId));

        var accOpt = queryService.handle(new GetAccountByIdQuery(id));
        if (accOpt.isEmpty()) return ResponseEntity.notFound().build();
        if (!isOwner(accOpt.get())) return ResponseEntity.status(403).body("Forbidden");

        try {
            var cmd = new WithdrawCommand(id, req.amount());
            return commandService.handle(cmd)
                    .<ResponseEntity<?>>map(a -> ResponseEntity.ok(toResponse(a)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
    // -------- FREEZE (valida ownership) --------
    @PutMapping("/{accountId}/freeze")
    public ResponseEntity<?> freeze(@PathVariable String accountId) {
        var id = new AccountId(UUID.fromString(accountId));

        var accOpt = queryService.handle(new GetAccountByIdQuery(id));
        if (accOpt.isEmpty()) return ResponseEntity.notFound().build();
        if (!isOwner(accOpt.get())) return ResponseEntity.status(403).body("Forbidden");

        try {
            var cmd = new FreezeAccountCommand(id);
            return commandService.handle(cmd)
                    .<ResponseEntity<?>>map(a -> ResponseEntity.ok(toResponse(a)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // -------- UNFREEZE (valida ownership) --------
    @PutMapping("/{accountId}/unfreeze")
    public ResponseEntity<?> unfreeze(@PathVariable String accountId) {
        var id = new AccountId(UUID.fromString(accountId));

        var accOpt = queryService.handle(new GetAccountByIdQuery(id));
        if (accOpt.isEmpty()) return ResponseEntity.notFound().build();
        if (!isOwner(accOpt.get())) return ResponseEntity.status(403).body("Forbidden");

        try {
            var cmd = new UnfreezeAccountCommand(id);
            return commandService.handle(cmd)
                    .<ResponseEntity<?>>map(a -> ResponseEntity.ok(toResponse(a)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // -------- CLOSE (valida ownership) --------
    @PutMapping("/{accountId}/close")
    public ResponseEntity<?> close(@PathVariable String accountId) {
        var id = new AccountId(UUID.fromString(accountId));

        var accOpt = queryService.handle(new GetAccountByIdQuery(id));
        if (accOpt.isEmpty()) return ResponseEntity.notFound().build();
        if (!isOwner(accOpt.get())) return ResponseEntity.status(403).body("Forbidden");

        try {
            var cmd = new CloseAccountCommand(id);
            return commandService.handle(cmd)
                    .<ResponseEntity<?>>map(a -> ResponseEntity.ok(toResponse(a)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    */
    private AccountResponse toResponse(BankAccount a) {
        return new AccountResponse(
                a.id().value().toString(),
                a.ownerId().value().toString(),
                a.accountNumber().value(),
                a.type().name(),
                a.status().name(),
                a.currency().name(),
                a.balance().amount().toPlainString()
        );
    }
}
