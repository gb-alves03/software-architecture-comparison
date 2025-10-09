package com.architecture.account_service.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.architecture.account_service.dto.NotificationDTO;
import com.architecture.account_service.enumeration.TransactionType;
import com.architecture.account_service.model.Transaction;

@Component
public class NotificationService {

    private final WebClient http;
    @Value("${notification.url}")
    private String notificationUrl;

    public NotificationService(WebClient.Builder http) {
        this.http = http.build();
    }

    public void sendNotification(Transaction transaction) {
        try {
            NotificationDTO.Request requestFrom = new NotificationDTO.Request(transaction.getFrom().getAccountId(),
                    "Transaction success: " + transaction.getTransactionId());

            send(requestFrom);

            if (transaction.getType() == TransactionType.TRANSFER) {
                NotificationDTO.Request requestTo = new NotificationDTO.Request(transaction.getTo().getAccountId(),
                        "Transaction success: " + transaction.getTransactionId());
                
                send(requestTo);
            }
        } catch (Exception e) {
            throw new RuntimeException("Notification failed: " + e.getMessage());
        }
    }
    
    private void send(NotificationDTO.Request request) {
        this.http.post()
            .uri("/v1/notification/stable")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }
}
