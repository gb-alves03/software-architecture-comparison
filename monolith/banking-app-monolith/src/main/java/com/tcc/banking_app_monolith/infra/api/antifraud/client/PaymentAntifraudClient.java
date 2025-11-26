package com.tcc.banking_app_monolith.infra.api.antifraud.client;

import com.tcc.banking_app_monolith.domain.entity.Payment;
import com.tcc.banking_app_monolith.infra.api.antifraud.dto.request.AntifraudRequestDto;
import com.tcc.banking_app_monolith.infra.api.antifraud.dto.response.AntifraudResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentAntifraudClient {

    private final RestClient restClient;

    public PaymentAntifraudClient(RestClient restClient,
                                  @Value("${antifraud.url}") String url) {
        this.restClient = restClient.mutate().baseUrl(url).build();
    }

    public boolean isFraudulent(Payment payment) {
        try {
            AntifraudRequestDto request = new AntifraudRequestDto("" + payment.getId(), payment.getAmount(),
                    payment.getPaymentType().toString(),
                    "" + payment.getAccountId());

            AntifraudResponseDto response =
                    this.restClient.post()
                            .uri(uriBuilder -> uriBuilder.path("/v1/fraud/validate").build())
                            .body(request)
                            .retrieve()
                            .body(AntifraudResponseDto.class);

            return response != null && response.isFraudulent();
        } catch (Exception e) {
            throw new RuntimeException("Transaction failed: " + e.getMessage());
        }
    }
}
