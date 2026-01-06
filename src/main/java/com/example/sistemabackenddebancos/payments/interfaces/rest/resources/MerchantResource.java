package com.example.sistemabackenddebancos.payments.interfaces.rest.resources;

import com.example.sistemabackenddebancos.payments.domain.model.enumerations.Category;
import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.MerchantInfo;
import com.example.sistemabackenddebancos.payments.interfaces.rest.dtos.requests.MerchantPayRequest;
import com.example.sistemabackenddebancos.payments.interfaces.rest.dtos.responses.MerchantPayResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/v1/merchant")
public class MerchantResource {


    // Catálogo simulado
    private static final List<MerchantInfo> MERCHANTS = List.of(
            // Universities
            new MerchantInfo("UNIVERSITY_UPC", "Universidad Peruana de Ciencias Aplicadas (UPC)", Category.UNIVERSITY),
            new MerchantInfo("UNIVERSITY_UTP", "Universidad Tecnológica del Perú (UTP)", Category.UNIVERSITY),
            new MerchantInfo("UNIVERSITY_PUCP", "Pontificia Universidad Católica del Perú (PUCP)", Category.UNIVERSITY),

            // Water
            new MerchantInfo("WATER_SEDAPAL", "SEDAPAL", Category.WATER),
            new MerchantInfo("WATER_SEDALIB", "SEDALIB", Category.WATER),

            // Electricity
            new MerchantInfo("ELECTRICITY_ENEL", "ENEL", Category.ELECTRICITY),
            new MerchantInfo("ELECTRICITY_LUZ_DEL_SUR", "Luz del Sur", Category.ELECTRICITY),

            // Internet
            new MerchantInfo("INTERNET_MOVISTAR", "Movistar", Category.INTERNET),
            new MerchantInfo("INTERNET_CLARO", "Claro", Category.INTERNET),
            new MerchantInfo("INTERNET_WIN", "WIN", Category.INTERNET)
    );

    private static final Map<String, MerchantInfo> BY_CODE = new HashMap<>();
    static {
        for (var m : MERCHANTS) BY_CODE.put(m.code(), m);
    }

    @GetMapping("/categories")
    public List<String> categories() {
        return Arrays.stream(Category.values()).map(Enum::name).toList();
    }

    @GetMapping("/merchants")
    public ResponseEntity<?> merchantsByCategory(@RequestParam String category) {
        Category cat;
        try {
            cat = Category.valueOf(category);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Unknown category");
        }

        var list = MERCHANTS.stream().filter(m -> m.category() == cat).toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestBody MerchantPayRequest req) {

        // Validaciones base
        if (req.merchantCode() == null || req.merchantCode().isBlank()) {
            return ResponseEntity.badRequest().body(new MerchantPayResponse("FAILED", "Missing merchantCode"));
        }
        if (req.reference() == null || req.reference().isBlank()) {
            return ResponseEntity.badRequest().body(new MerchantPayResponse("FAILED", "Missing reference"));
        }
        if (req.amount() == null || req.amount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(new MerchantPayResponse("FAILED", "Invalid amount"));
        }
        if (req.customerRef() == null || req.customerRef().isBlank()) {
            return ResponseEntity.badRequest().body(new MerchantPayResponse("FAILED", "Missing customerRef"));
        }

        var merchant = BY_CODE.get(req.merchantCode().trim());
        if (merchant == null) {
            return ResponseEntity.badRequest().body(new MerchantPayResponse("FAILED", "Unknown merchantCode"));
        }

        // Validación por categoría (simulada)
        String customerRef = req.customerRef().trim();

        switch (merchant.category()) {
            case UNIVERSITY -> {
                // Ejemplo: códigos tipo U20231C168 (flexible)
                if (!customerRef.matches("^[Uu][0-9A-Za-z]{6,15}$")) {
                    return ResponseEntity.ok(new MerchantPayResponse("FAILED", "Invalid university student code"));
                }
            }
            case WATER -> {
                // Ejemplo: NIS_123456789 o SOLO dígitos 8-12
                if (!customerRef.matches("^(NIS_)?\\d{8,12}$")) {
                    return ResponseEntity.ok(new MerchantPayResponse("FAILED", "Invalid water supply number"));
                }
            }
            case ELECTRICITY -> {
                if (!customerRef.matches("^(NIS_)?\\d{8,12}$")) {
                    return ResponseEntity.ok(new MerchantPayResponse("FAILED", "Invalid electricity supply number"));
                }
            }
            case INTERNET -> {
                // Ejemplo: número de servicio o teléfono
                if (!customerRef.matches("^\\d{8,12}$")) {
                    return ResponseEntity.ok(new MerchantPayResponse("FAILED", "Invalid internet service number"));
                }
            }
        }

        // Simulación de límite del merchant
        if (req.amount().compareTo(new BigDecimal("1500")) > 0) {
            return ResponseEntity.ok(new MerchantPayResponse("FAILED", "Amount exceeds merchant limit"));
        }

        return ResponseEntity.ok(new MerchantPayResponse("SUCCESS", null));
    }
}