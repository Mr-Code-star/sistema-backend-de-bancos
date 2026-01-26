package com.example.sistemabackenddebancos.transfers.interfaces.rest.resources;

import com.example.sistemabackenddebancos.accounts.domain.model.queries.*;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountId;
import com.example.sistemabackenddebancos.accounts.domain.services.AccountQueryService;
import com.example.sistemabackenddebancos.shared.domain.model.enumerations.Currency;
import com.example.sistemabackenddebancos.transfers.domain.model.aggregates.Transfer;
import com.example.sistemabackenddebancos.transfers.domain.model.commands.*;
import com.example.sistemabackenddebancos.transfers.domain.model.queries.GetTransferByIdQuery;
import com.example.sistemabackenddebancos.transfers.domain.model.queries.GetTransfersByAccountIdQuery;
import com.example.sistemabackenddebancos.transfers.domain.model.valueobjects.TransferId;
import com.example.sistemabackenddebancos.transfers.domain.model.valueobjects.TransferReference;
import com.example.sistemabackenddebancos.transfers.domain.services.TransferCommandService;
import com.example.sistemabackenddebancos.transfers.domain.services.TransferQueryService;
import com.example.sistemabackenddebancos.transfers.interfaces.rest.dtos.requests.*;
import com.example.sistemabackenddebancos.transfers.interfaces.rest.dtos.responses.TransferResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
@SecurityRequirement(name = "bearerAuth")
public class TransferResource {

    private final TransferCommandService commandService;
    private final TransferQueryService queryService;

    // Para ownership check (cuentas)
    private final AccountQueryService accountQueryService;

    public TransferResource(TransferCommandService commandService,
                            TransferQueryService queryService,
                            AccountQueryService accountQueryService) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.accountQueryService = accountQueryService;
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) throw new IllegalStateException("Unauthenticated");
        return UUID.fromString(auth.getPrincipal().toString());
    }

    private boolean isOwnerOfAccount(UUID accountId) {
        var accOpt = accountQueryService.handle(new GetAccountByIdQuery(new AccountId(accountId)));
        if (accOpt.isEmpty()) return false;
        return accOpt.get().ownerId().value().equals(currentUserId());
    }

    // -------- CREATE TRANSFER --------
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateTransferRequest req) {

        UUID fromId = UUID.fromString(req.fromAccountId());
        UUID toId = UUID.fromString(req.toAccountId());

        // ✅ Seguridad: solo puedes transferir desde tu cuenta (fromAccountId)
        if (!isOwnerOfAccount(fromId)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        var cmd = new CreateTransferCommand(
                new TransferReference(req.reference()),
                fromId,
                toId,
                Currency.valueOf(req.currency()),
                req.amount()
        );

        var result = commandService.handle(cmd);
        return result
                .<ResponseEntity<?>>map(t -> ResponseEntity.status(201).body(toResponse(t)))
                .orElseGet(() -> ResponseEntity.badRequest().body("Could not create transfer"));
    }

    // -------- GET TRANSFER BY ID (owner check via fromAccountId) --------
    @GetMapping("/{transferId}")
    public ResponseEntity<?> getById(@PathVariable String transferId) {
        var q = new GetTransferByIdQuery(new TransferId(UUID.fromString(transferId)));
        var opt = queryService.handle(q);

        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        var t = opt.get();

        // ✅ Seguridad: solo si eres dueño del fromAccountId (origen)
        if (!isOwnerOfAccount(t.fromAccountId())) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        return ResponseEntity.ok(toResponse(t));
    }

    // -------- LIST TRANSFERS BY ACCOUNT (owner check) --------
    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<?> getByAccount(@PathVariable String accountId) {
        UUID accId = UUID.fromString(accountId);

        // ✅ Seguridad: solo dueño de la cuenta puede ver su historial
        if (!isOwnerOfAccount(accId)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        var list = queryService.handle(new GetTransfersByAccountIdQuery(accId))
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(list);
    }

    private TransferResponse toResponse(Transfer t) {
        return new TransferResponse(
                t.id().value().toString(),
                t.reference().value(),
                t.fromAccountId().toString(),
                t.toAccountId().toString(),
                t.currency().name(),
                t.amount().toPlainString(),
                t.status().name(),
                t.failureReason()
        );
    }
}