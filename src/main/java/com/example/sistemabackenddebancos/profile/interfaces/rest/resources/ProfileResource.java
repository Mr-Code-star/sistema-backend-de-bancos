package com.example.sistemabackenddebancos.profile.interfaces.rest.resources;

import com.example.sistemabackenddebancos.profile.domain.model.commands.*;
import com.example.sistemabackenddebancos.profile.domain.model.enumerations.AddressType;
import com.example.sistemabackenddebancos.profile.domain.model.enumerations.DocumentType;
import com.example.sistemabackenddebancos.profile.domain.model.queries.GetProfileByIdQuery;
import com.example.sistemabackenddebancos.profile.domain.model.queries.GetProfileByUserIdQuery;
import com.example.sistemabackenddebancos.profile.domain.model.valueobjects.*;
import com.example.sistemabackenddebancos.profile.domain.services.ProfileCommandService;
import com.example.sistemabackenddebancos.profile.domain.services.ProfileQueryService;
import com.example.sistemabackenddebancos.profile.interfaces.rest.dtos.requests.*;
import com.example.sistemabackenddebancos.profile.interfaces.rest.dtos.responses.AddressResponse;
import com.example.sistemabackenddebancos.profile.interfaces.rest.dtos.responses.ProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileResource {

    private final ProfileCommandService commandService;
    private final ProfileQueryService queryService;

    public ProfileResource(ProfileCommandService commandService, ProfileQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    // -------- CREATE PROFILE --------
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateProfileRequest req) {
        var userId = new UserId(UUID.fromString(req.userId()));
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
                .orElseGet(() -> ResponseEntity.badRequest().body("Profile already exists for this userId"));
    }

    // -------- GET BY PROFILE ID --------
    @GetMapping("/{profileId}")
    public ResponseEntity<?> getById(@PathVariable String profileId) {
        var q = new GetProfileByIdQuery(new ProfileId(UUID.fromString(profileId)));
        return queryService.handle(q)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(toResponse(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------- GET BY USER ID --------
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<?> getByUserId(@PathVariable String userId) {
        var q = new GetProfileByUserIdQuery(new UserId(UUID.fromString(userId)));
        return queryService.handle(q)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(toResponse(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------- UPDATE BASIC INFO --------
    @PutMapping("/{profileId}")
    public ResponseEntity<?> update(@PathVariable String profileId, @RequestBody UpdateProfileRequest req) {
        var id = new ProfileId(UUID.fromString(profileId));
        var fullName = new FullName(req.givenNames(), req.paternalSurname(), req.maternalSurname());
        var phone = new PhoneNumber(req.phoneNumber());

        var cmd = new UpdateProfileCommand(id, fullName, phone);

        return commandService.handle(cmd)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(toResponse(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------- ADD ADDRESS --------
    @PostMapping("/{profileId}/addresses")
    public ResponseEntity<?> addAddress(@PathVariable String profileId, @RequestBody AddAddressRequest req) {
        var cmd = new AddAddressCommand(
                new ProfileId(UUID.fromString(profileId)),
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

    // -------- UPDATE ADDRESS --------
    @PutMapping("/{profileId}/addresses/{addressId}")
    public ResponseEntity<?> updateAddress(@PathVariable String profileId,
                                           @PathVariable String addressId,
                                           @RequestBody UpdateAddressRequest req) {

        var cmd = new UpdateAddressCommand(
                new ProfileId(UUID.fromString(profileId)),
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

    // -------- DELETE ADDRESS --------
    @DeleteMapping("/{profileId}/addresses/{addressId}")
    public ResponseEntity<?> removeAddress(@PathVariable String profileId, @PathVariable String addressId) {
        var cmd = new RemoveAddressCommand(
                new ProfileId(UUID.fromString(profileId)),
                new AddressId(UUID.fromString(addressId))
        );

        return commandService.handle(cmd)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(toResponse(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------- SET PRIMARY ADDRESS --------
    @PutMapping("/{profileId}/addresses/{addressId}/primary")
    public ResponseEntity<?> setPrimary(@PathVariable String profileId, @PathVariable String addressId) {
        var cmd = new SetPrimaryAddressCommand(
                new ProfileId(UUID.fromString(profileId)),
                new AddressId(UUID.fromString(addressId))
        );

        return commandService.handle(cmd)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(toResponse(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
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
}
