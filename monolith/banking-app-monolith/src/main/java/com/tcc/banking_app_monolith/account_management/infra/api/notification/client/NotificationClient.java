package com.tcc.banking_app_monolith.account_management.infra.api.notification.client;

import com.tcc.banking_app_monolith.account_management.domain.entity.Transaction;
import com.tcc.banking_app_monolith.account_management.domain.enums.TransactionType;
import com.tcc.banking_app_monolith.account_management.infra.api.notification.dto.NotificationRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NotificationClient {
    private final RestClient restClient;

    public NotificationClient(RestClient restClient,
                              @Value("${notification.url}") String url) {
        this.restClient = restClient.mutate().baseUrl(url).build();
    }

    public void sendNotification(Transaction transaction) {
        try {
            sendFrom(transaction);
            sendTo(transaction);
        } catch (Exception e) {
            throw new RuntimeException("Notification failed: " + e.getMessage());
        }
    }

    private void sendFrom(Transaction transaction) {
        NotificationRequestDto requestFrom = new NotificationRequestDto(transaction.getFrom().getId(),
                "Transaction success: " + transaction.getId());

        send(requestFrom);
    }

    private void sendTo(Transaction transaction) {
        if (transaction.getType().equals(TransactionType.TRANSFER)) {
            NotificationRequestDto requestTo = new NotificationRequestDto(transaction.getTo().getId(),
                    "Transaction success: " + transaction.getId());
        send(requestTo);
        }
    }

    private void send(NotificationRequestDto request) {
        this.restClient.post()
                .uri(uriBuilder -> uriBuilder.path("/v1/notification/stable").build())
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
