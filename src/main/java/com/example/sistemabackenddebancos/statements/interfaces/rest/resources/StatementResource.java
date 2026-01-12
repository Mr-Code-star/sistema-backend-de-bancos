package com.example.sistemabackenddebancos.statements.interfaces.rest.resources;

import com.example.sistemabackenddebancos.accounts.domain.model.queries.GetAccountByIdQuery;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountId;
import com.example.sistemabackenddebancos.accounts.domain.services.AccountQueryService;
import com.example.sistemabackenddebancos.statements.application.export.StatementPdfExporter;
import com.example.sistemabackenddebancos.statements.domain.model.queries.GetFullStatementByAccountQuery;
import com.example.sistemabackenddebancos.statements.domain.model.queries.GetStatementByAccountQuery;
import com.example.sistemabackenddebancos.statements.domain.model.valueobjects.DateRange;
import com.example.sistemabackenddebancos.statements.domain.services.StatementQueryService;
import com.example.sistemabackenddebancos.statements.interfaces.rest.dtos.responses.StatementLineResponse;
import com.example.sistemabackenddebancos.statements.interfaces.rest.dtos.responses.StatementResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/statements")
@SecurityRequirement(name = "bearerAuth")
public class StatementResource {

    private final StatementQueryService statementQueryService;
    private final AccountQueryService accountQueryService;
    private final StatementPdfExporter pdfExporter;

    public StatementResource(StatementQueryService statementQueryService,
                             AccountQueryService accountQueryService, StatementPdfExporter pdfExporter) {
        this.statementQueryService = statementQueryService;
        this.accountQueryService = accountQueryService;
        this.pdfExporter = pdfExporter;
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

    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<?> byAccount(@PathVariable String accountId,
                                       @RequestParam String from,
                                       @RequestParam String to) {

        UUID accId = UUID.fromString(accountId);

        if (!isOwnerOfAccount(accId)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        var range = new DateRange(LocalDate.parse(from), LocalDate.parse(to));
        var statement = statementQueryService.handle(new GetStatementByAccountQuery(accId, range));

        var lines = statement.lines().stream()
                .map(l -> new StatementLineResponse(
                        l.timestamp().toString(),
                        l.type(),
                        l.source(),
                        l.amount().toPlainString(),
                        l.currency(),
                        l.reference()
                )).toList();

        return ResponseEntity.ok(new StatementResponse(
                statement.accountId().toString(),
                statement.range().from().toString(),
                statement.range().to().toString(),
                statement.currency(),
                statement.openingBalance().toPlainString(),
                statement.totalCredits().toPlainString(),
                statement.totalDebits().toPlainString(),
                statement.closingBalance().toPlainString(),
                lines
        ));
    }


    @GetMapping("/by-account/{accountId}/pdf")
    public ResponseEntity<?> pdfByAccount(@PathVariable String accountId,
                                          @RequestParam String from,
                                          @RequestParam String to) {

        UUID accId = UUID.fromString(accountId);

        if (!isOwnerOfAccount(accId)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        var range = new DateRange(LocalDate.parse(from), LocalDate.parse(to));
        var statement = statementQueryService.handle(new GetStatementByAccountQuery(accId, range));

        byte[] pdf = pdfExporter.export(statement);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"statement-" + accId + "-" + from + "-to-" + to + ".pdf\"")
                .body(pdf);
    }

    @GetMapping("/by-account/{accountId}/pdf/all")
    public ResponseEntity<?> pdfAll(@PathVariable String accountId) {

        UUID accId = UUID.fromString(accountId);

        if (!isOwnerOfAccount(accId)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        var statement = statementQueryService.handle(
                new GetFullStatementByAccountQuery(accId)
        );

        byte[] pdf = pdfExporter.export(statement);

        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"statement-" + accId + "-all.pdf\"")
                .body(pdf);
    }

}
