package com.example.sistemabackenddebancos.shared.interfaces.rest.resources;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthResource {

    @GetMapping("/api/v1/health")
    public Map<String, Object> health() {
        return Map.of("status", "ok");
    }
}