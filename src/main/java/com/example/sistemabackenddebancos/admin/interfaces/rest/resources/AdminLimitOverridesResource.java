package com.example.sistemabackenddebancos.admin.interfaces.rest.resources;

import com.example.sistemabackenddebancos.admin.application.services.AdminAuditService;
import com.example.sistemabackenddebancos.limits.domain.model.enumerations.OperationType;
import com.example.sistemabackenddebancos.limits.infrastructure.persistence.jpa.entities.LimitOverrideEntity;
import com.example.sistemabackenddebancos.limits.infrastructure.persistence.jpa.repositories.SpringDataLimitOverrideJpaRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/limits")
@SecurityRequirement(name = "bearerAuth")
public class AdminLimitOverridesResource {

    private final SpringDataLimitOverrideJpaRepository overrideRepo;
    private final AdminAuditService audit;

    public AdminLimitOverridesResource(SpringDataLimitOverrideJpaRepository overrideRepo, AdminAuditService audit) {
        this.overrideRepo = overrideRepo;
        this.audit = audit;
    }

    private UUID adminUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(auth.getPrincipal().toString());
    }

    public record UpsertOverrideRequest(
            String operationType,
            BigDecimal maxDailyAmount,
            int maxDailyCount,
            String reason
    ) {}

    @PutMapping("/{userId}/override")
    public ResponseEntity<?> upsertOverride(@PathVariable String userId, @RequestBody UpsertOverrideRequest req) {

        UUID uid = UUID.fromString(userId);
        OperationType op = OperationType.valueOf(req.operationType());

        var entityOpt = overrideRepo.findByUserIdAndOperationType(uid, op);

        var e = entityOpt.orElseGet(() -> {
            var x = new LimitOverrideEntity();
            x.setId(UUID.randomUUID());
            x.setUserId(uid);
            x.setOperationType(op);
            return x;
        });

        e.setMaxDailyAmount(req.maxDailyAmount());
        e.setMaxDailyCount(req.maxDailyCount());
        e.setReason(req.reason());
        e.setUpdatedAt(Instant.now());

        overrideRepo.save(e);

        audit.log(adminUserId(), "UPSERT_LIMIT_OVERRIDE", "LIMIT_OVERRIDE", e.getId(), req.reason());

        return ResponseEntity.ok(Map.of(
                "userId", uid.toString(),
                "operationType", op.name(),
                "maxDailyAmount", e.getMaxDailyAmount().toPlainString(),
                "maxDailyCount", e.getMaxDailyCount(),
                "reason", e.getReason(),
                "updatedAt", e.getUpdatedAt().toString()
        ));
    }
}