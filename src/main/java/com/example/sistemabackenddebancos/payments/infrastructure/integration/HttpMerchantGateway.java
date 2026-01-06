package com.example.sistemabackenddebancos.payments.infrastructure.integration;

import com.example.sistemabackenddebancos.payments.applications.integrations.MerchantGateway;
import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.MerchantCode;
import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.PaymentReference;
import com.example.sistemabackenddebancos.payments.interfaces.rest.dtos.requests.MerchantPayRequest;
import com.example.sistemabackenddebancos.payments.interfaces.rest.dtos.responses.MerchantPayResponse;
import com.example.sistemabackenddebancos.shared.domain.model.enumerations.Currency;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class HttpMerchantGateway implements MerchantGateway {

    private final RestClient restClient;

    public HttpMerchantGateway(@Value("${merchant.base-url:http://localhost:8080}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }



    @Override
    public MerchantResult pay(MerchantCode merchantCode,
                              PaymentReference reference,
                              BigDecimal amount,
                              Currency currency,
                              String customerRef) {
        try {
            var req = new MerchantPayRequest(
                    merchantCode.value(),
                    reference.value(),
                    customerRef,
                    currency.name(),
                    amount
            );

            ResponseEntity<MerchantPayResponse> resp = restClient.post()
                    .uri("/api/v1/merchant/pay")
                    .body(req)
                    .retrieve()
                    .toEntity(MerchantPayResponse.class);

            var body = resp.getBody();
            if (body == null) return MerchantResult.fail("Empty merchant response");

            if ("SUCCESS".equalsIgnoreCase(body.status())) return MerchantResult.ok();
            return MerchantResult.fail(body.reason() != null ? body.reason() : "Merchant rejected payment");

        } catch (Exception ex) {
            return MerchantResult.fail("Merchant service error: " + ex.getMessage());
        }
    }

}