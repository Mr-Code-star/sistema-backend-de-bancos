package com.example.sistemabackenddebancos.notifications.interfaces.dtos.resources;

import com.example.sistemabackenddebancos.notifications.domain.model.aggregates.Notification;
import com.example.sistemabackenddebancos.notifications.domain.model.commands.ArchiveNotificationCommand;
import com.example.sistemabackenddebancos.notifications.domain.model.commands.MarkAsReadCommand;
import com.example.sistemabackenddebancos.notifications.domain.model.queries.GetMyNotificationsQuery;
import com.example.sistemabackenddebancos.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.example.sistemabackenddebancos.notifications.domain.model.valueobjects.NotificationId;
import com.example.sistemabackenddebancos.notifications.domain.model.valueobjects.RecipientId;
import com.example.sistemabackenddebancos.notifications.domain.services.NotificationCommandService;
import com.example.sistemabackenddebancos.notifications.domain.services.NotificationQueryService;
import com.example.sistemabackenddebancos.notifications.interfaces.dtos.responses.NotificationResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationResource {

    private final NotificationQueryService queryService;
    private final NotificationCommandService commandService;

    public NotificationResource(NotificationQueryService queryService,
                                NotificationCommandService commandService) {
        this.queryService = queryService;
        this.commandService = commandService;
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) throw new IllegalStateException("Unauthenticated");
        return UUID.fromString(auth.getPrincipal().toString());
    }

    private boolean isOwner(Notification n) {
        return n.recipientId().value().equals(currentUserId());
    }

    // -------- GET MY NOTIFICATIONS --------
    @GetMapping("/my")
    public ResponseEntity<?> myNotifications() {
        var q = new GetMyNotificationsQuery(new RecipientId(currentUserId()));
        var list = queryService.handle(q).stream().map(this::toResponse).toList();
        return ResponseEntity.ok(list);
    }

    // -------- MARK AS READ --------
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable String id) {
        var q = new GetNotificationByIdQuery(new NotificationId(UUID.fromString(id)));
        var existing = queryService.handle(q);

        if (existing.isEmpty()) return ResponseEntity.notFound().build();
        if (!isOwner(existing.get())) return ResponseEntity.status(403).body("Forbidden");

        var cmd = new MarkAsReadCommand(new NotificationId(UUID.fromString(id)));
        return commandService.handle(cmd)
                .<ResponseEntity<?>>map(n -> ResponseEntity.ok(toResponse(n)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------- ARCHIVE --------
    @PutMapping("/{id}/archive")
    public ResponseEntity<?> archive(@PathVariable String id) {
        var q = new GetNotificationByIdQuery(new NotificationId(UUID.fromString(id)));
        var existing = queryService.handle(q);

        if (existing.isEmpty()) return ResponseEntity.notFound().build();
        if (!isOwner(existing.get())) return ResponseEntity.status(403).body("Forbidden");

        var cmd = new ArchiveNotificationCommand(new NotificationId(UUID.fromString(id)));
        return commandService.handle(cmd)
                .<ResponseEntity<?>>map(n -> ResponseEntity.ok(toResponse(n)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.id().value().toString(),
                n.recipientId().value().toString(),
                n.type().name(),
                n.channel().name(),
                n.title().value(),
                n.body().value(),
                n.status().name(),
                n.reference(),
                n.createdAt().toString()
        );
    }
}