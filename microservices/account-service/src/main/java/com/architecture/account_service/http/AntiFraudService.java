package com.architecture.account_service.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.architecture.account_service.dto.AntiFraudDTO;
import com.architecture.account_service.model.Transaction;

@Component
public class AntiFraudService {

    private final WebClient webClient;
    @Value("${antifraud.url}")
    private String antiFraudUrl;

    public AntiFraudService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public boolean isFraudulent(Transaction transaction) {
        try {
            AntiFraudDTO.Request request = new AntiFraudDTO.Request("" + transaction.getTransactionId(),
                    transaction.getAmount(), transaction.getType().toString(),
                    "" + transaction.getFrom().getAccountId());

            AntiFraudDTO.Response response = webClient.post().uri(antiFraudUrl + "/v1/fraud/validate")
                    .bodyValue(request).retrieve().bodyToMono(AntiFraudDTO.Response.class).block();

            return response != null && response.fraudulent();
        } catch (Exception e) {
            throw new RuntimeException("Transaction failed: " + e.getMessage());
        }
    }
}
