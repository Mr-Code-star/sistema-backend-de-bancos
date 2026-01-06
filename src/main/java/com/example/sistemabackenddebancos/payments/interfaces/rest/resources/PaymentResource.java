package com.example.sistemabackenddebancos.payments.interfaces.rest.resources;

import com.example.sistemabackenddebancos.accounts.domain.model.queries.GetAccountByIdQuery;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountId;
import com.example.sistemabackenddebancos.accounts.domain.services.AccountQueryService;
import com.example.sistemabackenddebancos.payments.applications.integrations.MerchantGateway;
import com.example.sistemabackenddebancos.payments.domain.model.commands.CreatePaymentCommand;
import com.example.sistemabackenddebancos.payments.domain.model.enumerations.PaymentType;
import com.example.sistemabackenddebancos.payments.domain.model.queries.GetPaymentByReferenceQuery;
import com.example.sistemabackenddebancos.payments.domain.model.queries.GetPaymentsByAccountIdQuery;
import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.MerchantCode;
import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.PaymentReference;
import com.example.sistemabackenddebancos.payments.domain.services.PaymentCommandService;
import com.example.sistemabackenddebancos.payments.domain.services.PaymentQueryService;
import com.example.sistemabackenddebancos.payments.interfaces.rest.dtos.requests.CreatePaymentRequest;
import com.example.sistemabackenddebancos.payments.interfaces.rest.dtos.responses.PaymentResponse;
import com.example.sistemabackenddebancos.shared.domain.model.enumerations.Currency;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@SecurityRequirement(name = "bearerAuth")
public class PaymentResource {
    private final PaymentCommandService commandService;
    private final PaymentQueryService queryService;

    // Para ownership check
    private final AccountQueryService accountQueryService;

    // Constructor
    public PaymentResource(PaymentCommandService commandService,
                           PaymentQueryService queryService,
                           AccountQueryService accountQueryService) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.accountQueryService = accountQueryService;

    }
    // Método auxiliar para obtener el ID del usuario autenticado
    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) throw new IllegalStateException("Unauthenticated");
        return UUID.fromString(auth.getPrincipal().toString());
    }

    // Método auxiliar para verificar si el usuario actual es el propietario de una cuenta
    private boolean isOwnerOfAccount(UUID accountId) {
        var accOpt = accountQueryService.handle(new GetAccountByIdQuery(new AccountId(accountId)));
        if (accOpt.isEmpty()) return false;
        return accOpt.get().ownerId().value().equals(currentUserId());
    }

    // -------- CREATE PAYMENT --------
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreatePaymentRequest req) {
        UUID fromAccountId = UUID.fromString(req.fromAccountId());

        // ✅ Seguridad: solo puedes pagar desde tu cuenta
        if (!isOwnerOfAccount(fromAccountId)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        var cmd = new CreatePaymentCommand(
                new PaymentReference(req.reference()),
                fromAccountId,
                new MerchantCode(req.merchantCode()),
                PaymentType.valueOf(req.type()),
                Currency.valueOf(req.currency()),
                req.amount(),
                req.customerRef()
        );

        var result = commandService.handle(cmd);

        return result
                .<ResponseEntity<?>>map(p -> ResponseEntity.status(201).body(toResponse(p)))
                .orElseGet(() -> ResponseEntity.badRequest().body("Could not create payment"));
    }

    // -------- GET BY REFERENCE (ownership check) --------
    @GetMapping("/by-reference/{reference}")
    public ResponseEntity<?> getByReference(@PathVariable String reference) {
        var q = new GetPaymentByReferenceQuery(new PaymentReference(reference));
        var opt = queryService.handle(q);

        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        var p = opt.get();
        if (!isOwnerOfAccount(p.fromAccountId())) return ResponseEntity.status(403).body("Forbidden");

        return ResponseEntity.ok(toResponse(p));
    }

    // -------- LIST BY ACCOUNT (ownership check) --------
    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<?> getByAccount(@PathVariable String accountId) {
        UUID accId = UUID.fromString(accountId);

        if (!isOwnerOfAccount(accId)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        var list = queryService.handle(new GetPaymentsByAccountIdQuery(accId))
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(list);
    }


    private PaymentResponse toResponse(com.example.sistemabackenddebancos.payments.domain.model.aggregates.Payment p) {
        return new PaymentResponse(
                p.id().value().toString(),
                p.reference().value(),
                p.fromAccountId().toString(),
                p.merchantCode().value(),
                p.type().name(),
                p.currency().name(),
                p.amount().toPlainString(),
                p.status().name(),
                p.failureReason()
        );
    }

}

