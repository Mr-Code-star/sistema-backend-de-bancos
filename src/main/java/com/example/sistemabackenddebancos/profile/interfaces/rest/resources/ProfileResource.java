package com.example.sistemabackenddebancos.profile.interfaces.rest.resources;

import com.example.sistemabackenddebancos.notifications.domain.model.enumerations.NotificationChannel;
import com.example.sistemabackenddebancos.notifications.domain.model.enumerations.NotificationType;
import com.example.sistemabackenddebancos.profile.domain.model.commands.*;
import com.example.sistemabackenddebancos.profile.domain.model.entities.NotificationPreferences;
import com.example.sistemabackenddebancos.profile.domain.model.enumerations.AddressType;
import com.example.sistemabackenddebancos.profile.domain.model.enumerations.DocumentType;
import com.example.sistemabackenddebancos.profile.domain.model.queries.GetNotificationPreferencesByUserIdQuery;
import com.example.sistemabackenddebancos.profile.domain.model.queries.GetProfileByIdQuery;
import com.example.sistemabackenddebancos.profile.domain.model.queries.GetProfileByUserIdQuery;
import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.*;
import com.example.sistemabackenddebancos.profile.domain.services.ProfileCommandService;
import com.example.sistemabackenddebancos.profile.domain.services.ProfileQueryService;
import com.example.sistemabackenddebancos.profile.interfaces.rest.dtos.requests.*;
import com.example.sistemabackenddebancos.profile.interfaces.rest.dtos.responses.AddressResponse;
import com.example.sistemabackenddebancos.profile.interfaces.rest.dtos.responses.NotificationPreferencesResponse;
import com.example.sistemabackenddebancos.profile.interfaces.rest.dtos.responses.ProfileResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/profiles")
@SecurityRequirement(name = "bearerAuth")
public class ProfileResource {

    private final ProfileCommandService commandService;
    private final ProfileQueryService queryService;

    public ProfileResource(ProfileCommandService commandService, ProfileQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    // ✅ userId viene SIEMPRE del JWT (subject/principal)
    private UUID currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) throw new IllegalStateException("Unauthenticated");
        return UUID.fromString(auth.getPrincipal().toString());
    }

    private boolean isOwner(com.example.sistemabackenddebancos.profile.domain.model.aggregates.Profile p) {
        return p.userId().value().equals(currentUserId());
    }

    // -------- CREATE PROFILE (userId desde JWT) --------
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateProfileRequest req) {
        var userId = new UserId(currentUserId());
        var fullName = new FullName(req.givenNames(), req.paternalSurname(), req.maternalSurname());
        var phone = new PhoneNumber(req.phoneNumber());

        DocumentNumber doc = null;
        if (req.documentType() != null && !req.documentType().isBlank()
                && req.documentNumber() != null && !req.documentNumber().isBlank()) {
            doc = new DocumentNumber(DocumentType.valueOf(req.documentType()), req.documentNumber());
        }

        var cmd = new CreateProfileCommand(userId, fullName, phone, doc);

        return commandService.handle(cmd)
                .<ResponseEntity<?>>map(p -> ResponseEntity.status(201).body(toResponse(p)))
                .orElseGet(() -> ResponseEntity.badRequest().body("Profile already exists for this user"));
    }

    // -------- GET MY PROFILE --------
    @GetMapping("/me")
    public ResponseEntity<?> getMe() {
        var q = new GetProfileByUserIdQuery(new UserId(currentUserId()));
        return queryService.handle(q)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(toResponse(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------- GET BY PROFILE ID (ownership check) --------
    @GetMapping("/{profileId}")
    public ResponseEntity<?> getById(@PathVariable String profileId) {
        var q = new GetProfileByIdQuery(new ProfileId(UUID.fromString(profileId)));
        var opt = queryService.handle(q);

        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        if (!isOwner(opt.get())) return ResponseEntity.status(403).body("Forbidden");

        return ResponseEntity.ok(toResponse(opt.get()));
    }

    // -------- UPDATE BASIC INFO (ownership check) --------
    @PutMapping("/{profileId}")
    public ResponseEntity<?> update(@PathVariable String profileId, @RequestBody UpdateProfileRequest req) {
        var id = new ProfileId(UUID.fromString(profileId));

        var existing = queryService.handle(new GetProfileByIdQuery(id));
        if (existing.isEmpty()) return ResponseEntity.notFound().build();
        if (!isOwner(existing.get())) return ResponseEntity.status(403).body("Forbidden");

        var fullName = new FullName(req.givenNames(), req.paternalSurname(), req.maternalSurname());
        var phone = new PhoneNumber(req.phoneNumber());

        var cmd = new UpdateProfileCommand(id, fullName, phone);

        return commandService.handle(cmd)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(toResponse(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------- ADD ADDRESS (ownership check) --------
    @PostMapping("/{profileId}/addresses")
    public ResponseEntity<?> addAddress(@PathVariable String profileId, @RequestBody AddAddressRequest req) {
        var id = new ProfileId(UUID.fromString(profileId));

        var existing = queryService.handle(new GetProfileByIdQuery(id));
        if (existing.isEmpty()) return ResponseEntity.notFound().build();
        if (!isOwner(existing.get())) return ResponseEntity.status(403).body("Forbidden");

        var cmd = new AddAddressCommand(
                id,
                AddressType.valueOf(req.type()),
                req.line1(),
                req.line2(),
                req.city(),
                req.region(),
                req.country(),
                req.postalCode(),
                req.makePrimary()
        );

        return commandService.handle(cmd)
                .<ResponseEntity<?>>map(p -> ResponseEntity.status(201).body(toResponse(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------- UPDATE ADDRESS (ownership check) --------
    @PutMapping("/{profileId}/addresses/{addressId}")
    public ResponseEntity<?> updateAddress(@PathVariable String profileId,
                                           @PathVariable String addressId,
                                           @RequestBody UpdateAddressRequest req) {

        var id = new ProfileId(UUID.fromString(profileId));

        var existing = queryService.handle(new GetProfileByIdQuery(id));
        if (existing.isEmpty()) return ResponseEntity.notFound().build();
        if (!isOwner(existing.get())) return ResponseEntity.status(403).body("Forbidden");

        var cmd = new UpdateAddressCommand(
                id,
                new AddressId(UUID.fromString(addressId)),
                AddressType.valueOf(req.type()),
                req.line1(),
                req.line2(),
                req.city(),
                req.region(),
                req.country(),
                req.postalCode()
        );

        return commandService.handle(cmd)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(toResponse(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------- DELETE ADDRESS (ownership check) --------
    @DeleteMapping("/{profileId}/addresses/{addressId}")
    public ResponseEntity<?> removeAddress(@PathVariable String profileId, @PathVariable String addressId) {
        var id = new ProfileId(UUID.fromString(profileId));

        var existing = queryService.handle(new GetProfileByIdQuery(id));
        if (existing.isEmpty()) return ResponseEntity.notFound().build();
        if (!isOwner(existing.get())) return ResponseEntity.status(403).body("Forbidden");

        var cmd = new RemoveAddressCommand(
                id,
                new AddressId(UUID.fromString(addressId))
        );

        return commandService.handle(cmd)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(toResponse(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------- SET PRIMARY ADDRESS (ownership check) --------
    @PutMapping("/{profileId}/addresses/{addressId}/primary")
    public ResponseEntity<?> setPrimary(@PathVariable String profileId, @PathVariable String addressId) {
        var id = new ProfileId(UUID.fromString(profileId));

        var existing = queryService.handle(new GetProfileByIdQuery(id));
        if (existing.isEmpty()) return ResponseEntity.notFound().build();
        if (!isOwner(existing.get())) return ResponseEntity.status(403).body("Forbidden");

        var cmd = new SetPrimaryAddressCommand(
                id,
                new AddressId(UUID.fromString(addressId))
        );

        return commandService.handle(cmd)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(toResponse(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/me/preferences/notifications")
    public ResponseEntity<?> getMyPreferences() {
        var q = new GetNotificationPreferencesByUserIdQuery(new UserId(currentUserId()));
        var profileOpt = queryService.handle(q);

        if (profileOpt.isEmpty()) return ResponseEntity.notFound().build();

        var prefs = profileOpt.get().notificationPreferences();
        return ResponseEntity.ok(toResponse(prefs));
    }

    // -------- UPDATE MY NOTIFICATION PREFERENCES --------
    @PutMapping("/me/preferences/notifications")
    public ResponseEntity<?> updateMyPreferences(@RequestBody UpdateNotificationPreferencesRequest req) {
        var q = new GetNotificationPreferencesByUserIdQuery(new UserId(currentUserId()));
        var profileOpt = queryService.handle(q);

        if (profileOpt.isEmpty()) return ResponseEntity.notFound().build();

        var profile = profileOpt.get();

        var prefs = toDomainPrefs(req);

        var cmd = new UpdateNotificationPreferencesCommand(profile.id(), prefs);

        return commandService.handle(cmd)
                .<ResponseEntity<?>>map(updated -> ResponseEntity.ok(toResponse(updated.notificationPreferences())))
                .orElseGet(() -> ResponseEntity.badRequest().body("Could not update preferences"));
    }

    // -------- Mapper to response --------
    private ProfileResponse toResponse(com.example.sistemabackenddebancos.profile.domain.model.aggregates.Profile p) {
        var addresses = p.addresses().stream()
                .map(a -> new AddressResponse(
                        a.id().value().toString(),
                        a.type().name(),
                        a.line1(),
                        a.line2(),
                        a.city(),
                        a.region(),
                        a.country(),
                        a.postalCode(),
                        a.primary()
                ))
                .toList();

        return new ProfileResponse(
                p.id().value().toString(),
                p.userId().value().toString(),
                p.fullName().givenNames(),
                p.fullName().paternalSurname(),
                p.fullName().maternalSurname(),
                p.phoneNumber().value(),
                p.documentNumber() != null ? p.documentNumber().type().name() : null,
                p.documentNumber() != null ? p.documentNumber().value() : null,
                p.kycStatus().name(),
                addresses
        );
    }

    private NotificationPreferences toDomainPrefs(UpdateNotificationPreferencesRequest req) {
        Map<NotificationType, Set<NotificationChannel>> map = new EnumMap<>(NotificationType.class);

        if (req.channelsByType() != null) {
            for (var entry : req.channelsByType().entrySet()) {
                var type = NotificationType.valueOf(entry.getKey());
                var channels = EnumSet.noneOf(NotificationChannel.class);

                for (var ch : entry.getValue()) {
                    channels.add(NotificationChannel.valueOf(ch));
                }
                map.put(type, channels);
            }
        }

        // si faltan tipos, el VO se encarga de defaults (IN_APP)
        return new NotificationPreferences(req.emailEnabled(), req.smsEnabled(), map);
    }

    private NotificationPreferencesResponse toResponse(NotificationPreferences prefs) {
        Map<String, List<String>> map = new LinkedHashMap<>();

        for (var type : NotificationType.values()) {
            var channels = prefs.channelsFor(type).stream().map(Enum::name).toList();
            map.put(type.name(), channels);
        }

        return new NotificationPreferencesResponse(
                prefs.emailEnabled(),
                prefs.smsEnabled(),
                map
        );
    }

}
