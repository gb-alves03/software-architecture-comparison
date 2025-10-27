package com.tcc.payment_service.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.architecture.account_service.dto.AntiFraudDTO;
import com.architecture.account_service.model.Transaction;
import com.tcc.payment_service.model.Payment;

@Component
public class AntiFraudService {

    private final WebClient http;
    @Value("${antifraud.url}")
    private String antiFraudUrl;

    public AntiFraudService(WebClient.Builder http) {
        this.http = http.build();
    }

    public boolean isFraudulent(Payment payment) {
        try {
            AntiFraudDTO.Request request = new AntiFraudDTO.Request("" + payment.getPaymentId(),
                    payment.getAmount(), payment.getPaymentType().toString(),
                    "" + payment.getAccountId());

            AntiFraudDTO.Response response = http.post().uri(antiFraudUrl + "/v1/fraud/validate").bodyValue(request)
                    .retrieve().bodyToMono(AntiFraudDTO.Response.class).block();

            return response != null && response.fraudulent();
        } catch (Exception e) {
            throw new RuntimeException("Transaction failed: " + e.getMessage());
        }
    }
}
