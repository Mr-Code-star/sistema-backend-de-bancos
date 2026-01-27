package com.example.sistemabackenddebancos.admin.interfaces.rest.resources;

import com.example.sistemabackenddebancos.accounts.domain.model.enumerations.AccountStatus;
import com.example.sistemabackenddebancos.accounts.infrastructure.persistence.jpa.repositories.SpringDataAccountJpaRepository;
import com.example.sistemabackenddebancos.admin.infrastructrure.persitence.jpa.repositories.SpringDataAdminActionJpaRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;
import java.util.UUID;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@SecurityRequirement(name = "bearerAuth")
public class AdminDashboardResource {

    private final SpringDataAdminActionJpaRepository adminActionRepo;
    private final SpringDataAccountJpaRepository accountJpa;


    public AdminDashboardResource(SpringDataAdminActionJpaRepository adminActionRepo, SpringDataAccountJpaRepository accountJpa) {
        this.adminActionRepo = adminActionRepo;
        this.accountJpa = accountJpa;
    }


    @GetMapping("/actions")
    public ResponseEntity<?> actions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) String adminUserId
    ) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, Math.min(size, 200));

        var pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        var hasActionType = actionType != null && !actionType.isBlank();
        var hasAdminUserId = adminUserId != null && !adminUserId.isBlank();

        var resultPage = switch ((hasActionType ? 1 : 0) + (hasAdminUserId ? 2 : 0)) {
            case 0 -> adminActionRepo.findAllByOrderByCreatedAtDesc(pageable);
            case 1 -> adminActionRepo.findAllByActionTypeOrderByCreatedAtDesc(actionType.trim(), pageable);
            case 2 -> adminActionRepo.findAllByAdminUserIdOrderByCreatedAtDesc(UUID.fromString(adminUserId.trim()), pageable);
            case 3 -> adminActionRepo.findAllByActionTypeAndAdminUserIdOrderByCreatedAtDesc(
                    actionType.trim(),
                    UUID.fromString(adminUserId.trim()),
                    pageable
            );
            default -> adminActionRepo.findAllByOrderByCreatedAtDesc(pageable);
        };

        var list = resultPage.getContent().stream().map(a -> Map.of(
                "id", a.getId().toString(),
                "adminUserId", a.getAdminUserId().toString(),
                "actionType", a.getActionType(),
                "targetType", a.getTargetType(),
                "targetId", a.getTargetId().toString(),
                "reason", a.getReason(),
                "createdAt", a.getCreatedAt().toString()
        )).toList();

        return ResponseEntity.ok(Map.of(
                "page", safePage,
                "size", safeSize,
                "totalElements", resultPage.getTotalElements(),
                "totalPages", resultPage.getTotalPages(),
                "items", list
        ));
    }

}
