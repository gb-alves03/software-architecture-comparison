package com.tcc.banking_app_monolith.account_management.infra.api.antifraud.client;

import com.tcc.banking_app_monolith.account_management.domain.entity.Transaction;
import com.tcc.banking_app_monolith.account_management.infra.api.antifraud.dto.request.AntifraudRequestDto;
import com.tcc.banking_app_monolith.account_management.infra.api.antifraud.dto.response.AntifraudResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AntifraudClient {

    private final RestClient restClient;

    public AntifraudClient(RestClient restClient,
                           @Value("${antifraud.url}") String url) {
        this.restClient = restClient.mutate().baseUrl(url).build();
    }

    public boolean isFraudulent(Transaction transaction) {
        try {
            AntifraudRequestDto request = new AntifraudRequestDto("" + transaction.getId(), transaction.getAmount(),
                    transaction.getType().toString(),
                    "" + transaction.getFrom().getId());

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
