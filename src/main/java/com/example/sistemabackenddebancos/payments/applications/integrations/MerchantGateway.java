package com.example.sistemabackenddebancos.payments.applications.integrations;

import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.MerchantCode;
import com.example.sistemabackenddebancos.payments.domain.model.valueobjects.PaymentReference;
import com.example.sistemabackenddebancos.shared.domain.model.enumerations.Currency;

import java.math.BigDecimal;
import java.util.List;

public interface MerchantGateway {

    record MerchantResult(boolean success, String failureReason) {
        public static MerchantResult ok() { return new MerchantResult(true, null); }
        public static MerchantResult fail(String reason) { return new MerchantResult(false, reason); }
    }


    MerchantResult pay(MerchantCode merchantCode,
                       PaymentReference reference,
                       BigDecimal amount,
                       Currency currency,
                       String customerRef);
}