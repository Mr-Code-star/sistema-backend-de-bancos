package com.example.sistemabackenddebancos.admin.interfaces.rest.resources;

import com.example.sistemabackenddebancos.admin.application.services.AdminLimitService;
import com.example.sistemabackenddebancos.limits.domain.model.enumerations.OperationType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/limits")
@SecurityRequirement(name = "bearerAuth")
public class AdminLimitsResource {

    private final AdminLimitService adminLimitService;

    public AdminLimitsResource(AdminLimitService adminLimitService) {
        this.adminLimitService = adminLimitService;
    }

    // Delete /api/v1/admin/limits/{userId}/today?operation=TRANSFER
    @DeleteMapping("/{userId}/today")
    public ResponseEntity<?> resetToday(@PathVariable String userId,
                                        @RequestParam(required = false) String operation) {

        UUID uid = UUID.fromString(userId);

        OperationType op = null;

        if (operation != null && !operation.isBlank()){
            op = OperationType.valueOf(operation);
        }

        long deleted = adminLimitService.resetToday(uid, op);

        return ResponseEntity.ok(Map.of(
           "userId", uid.toString(),
           "operation", op != null ? op.name() : "All",
           "recordsDeleted",    deleted
        ));
    }

}
