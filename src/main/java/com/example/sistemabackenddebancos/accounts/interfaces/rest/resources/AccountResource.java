package com.example.sistemabackenddebancos.accounts.interfaces.rest.resources;

import com.example.sistemabackenddebancos.accounts.domain.model.commands.*;
import com.example.sistemabackenddebancos.accounts.domain.model.enumerations.AccountType;
import com.example.sistemabackenddebancos.accounts.domain.model.enumerations.Currency;
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

    // -------- OPEN ACCOUNT --------
    @PostMapping
    public ResponseEntity<?> open(@RequestBody OpenAccountRequest req) {
        var cmd = new OpenAccountCommand(
                new OwnerId(UUID.fromString(req.ownerId())),
                AccountType.valueOf(req.type()),
                Currency.valueOf(req.currency())
        );

        return commandService.handle(cmd)
                .<ResponseEntity<?>>map(a -> ResponseEntity.status(201).body(toResponse(a)))
                .orElseGet(() -> ResponseEntity.badRequest().body("Could not open account (number collision or invalid data)"));
    }

    // -------- GET BY ID --------
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getById(@PathVariable String accountId) {
        var q = new GetAccountByIdQuery(new AccountId(UUID.fromString(accountId)));

        return queryService.handle(q)
                .<ResponseEntity<?>>map(a -> ResponseEntity.ok(toResponse(a)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------- GET BY NUMBER --------
    @GetMapping("/by-number/{accountNumber}")
    public ResponseEntity<?> getByNumber(@PathVariable String accountNumber) {
        var q = new GetAccountByNumberQuery(new AccountNumber(accountNumber));

        return queryService.handle(q)
                .<ResponseEntity<?>>map(a -> ResponseEntity.ok(toResponse(a)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------- GET BY OWNER --------
    @GetMapping("/by-owner/{ownerId}")
    public ResponseEntity<?> getByOwner(@PathVariable String ownerId) {
        var q = new GetAccountsByOwnerIdQuery(new OwnerId(UUID.fromString(ownerId)));
        var list = queryService.handle(q).stream().map(this::toResponse).toList();
        return ResponseEntity.ok(list);
    }

    // -------- DEPOSIT --------
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<?> deposit(@PathVariable String accountId, @RequestBody AmountRequest req) {
        var cmd = new DepositCommand(new AccountId(UUID.fromString(accountId)), req.amount());

        try {
            return commandService.handle(cmd)
                    .<ResponseEntity<?>>map(a -> ResponseEntity.ok(toResponse(a)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // -------- WITHDRAW --------
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable String accountId, @RequestBody AmountRequest req) {
        var cmd = new WithdrawCommand(new AccountId(UUID.fromString(accountId)), req.amount());

        try {
            return commandService.handle(cmd)
                    .<ResponseEntity<?>>map(a -> ResponseEntity.ok(toResponse(a)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // -------- FREEZE --------
    @PutMapping("/{accountId}/freeze")
    public ResponseEntity<?> freeze(@PathVariable String accountId) {
        var cmd = new FreezeAccountCommand(new AccountId(UUID.fromString(accountId)));

        try {
            return commandService.handle(cmd)
                    .<ResponseEntity<?>>map(a -> ResponseEntity.ok(toResponse(a)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // -------- UNFREEZE --------
    @PutMapping("/{accountId}/unfreeze")
    public ResponseEntity<?> unfreeze(@PathVariable String accountId) {
        var cmd = new UnfreezeAccountCommand(new AccountId(UUID.fromString(accountId)));

        try {
            return commandService.handle(cmd)
                    .<ResponseEntity<?>>map(a -> ResponseEntity.ok(toResponse(a)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // -------- CLOSE --------
    @PutMapping("/{accountId}/close")
    public ResponseEntity<?> close(@PathVariable String accountId) {
        var cmd = new CloseAccountCommand(new AccountId(UUID.fromString(accountId)));

        try {
            return commandService.handle(cmd)
                    .<ResponseEntity<?>>map(a -> ResponseEntity.ok(toResponse(a)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // -------- Mapper --------
    private AccountResponse toResponse(com.example.sistemabackenddebancos.accounts.domain.model.aggregates.BankAccount a) {
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