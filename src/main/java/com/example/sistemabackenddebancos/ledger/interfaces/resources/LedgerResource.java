package com.example.sistemabackenddebancos.ledger.interfaces.rest.resources;

import com.example.sistemabackenddebancos.accounts.domain.model.queries.GetAccountByIdQuery;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountId;
import com.example.sistemabackenddebancos.accounts.domain.services.AccountQueryService;
import com.example.sistemabackenddebancos.ledger.domain.model.queries.GetEntriesByAccountIdQuery;
import com.example.sistemabackenddebancos.ledger.domain.model.queries.GetEntriesByReferenceQuery;
import com.example.sistemabackenddebancos.ledger.domain.model.valueobjects.TransactionReference;
import com.example.sistemabackenddebancos.ledger.domain.services.LedgerQueryService;
import com.example.sistemabackenddebancos.ledger.interfaces.rest.dtos.responses.LedgerEntryResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ledger")
@SecurityRequirement(name = "bearerAuth")
public class LedgerResource {

    private final LedgerQueryService ledgerQueryService;
    private final AccountQueryService accountQueryService;

    public LedgerResource(LedgerQueryService ledgerQueryService,
                          AccountQueryService accountQueryService) {
        this.ledgerQueryService = ledgerQueryService;
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

    // -------- BY ACCOUNT (ownership check) --------
    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<?> getByAccount(@PathVariable String accountId) {
        UUID accId = UUID.fromString(accountId);

        if (!isOwnerOfAccount(accId)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        var entries = ledgerQueryService.handle(new GetEntriesByAccountIdQuery(accId))
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(entries);
    }

    // -------- BY REFERENCE (ownership check) --------
    @GetMapping("/by-reference/{reference}")
    public ResponseEntity<?> getByReference(@PathVariable String reference) {

        var ref = new TransactionReference(reference);

        var entries = ledgerQueryService.handle(new GetEntriesByReferenceQuery(ref));

        // Seguridad: el usuario debe ser dueño de al menos una cuenta dentro de esas entries
        boolean allowed = entries.stream().anyMatch(e -> isOwnerOfAccount(e.accountId()));
        if (!allowed) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        var response = entries.stream().map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    private LedgerEntryResponse toResponse(com.example.sistemabackenddebancos.ledger.domain.model.aggregates.LedgerEntry e) {
        return new LedgerEntryResponse(
                e.id().value().toString(),
                e.accountId().toString(),
                e.type().name(),
                e.source().name(),
                e.currency().name(),
                e.amount().toPlainString(),
                e.reference().value(),
                e.createdAt().toString()
        );
    }
}
