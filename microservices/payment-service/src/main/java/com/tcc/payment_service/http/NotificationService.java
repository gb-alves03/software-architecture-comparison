package com.tcc.payment_service.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.tcc.payment_service.dto.NotificationDTO;
import com.tcc.payment_service.model.Payment;

@Component
public class NotificationService {

    private final WebClient http;
    @Value("${notification.url}")
    private String notificationUrl;

    public NotificationService(WebClient.Builder http) {
        this.http = http.build();
    }

    public void sendNotification(Payment payment) {
        try {
            NotificationDTO.Request requestFrom = new NotificationDTO.Request(payment.getAccountId(),
                    "Transaction success: " + payment.getAccountId());

            send(requestFrom);
        } catch (Exception e) {
            throw new RuntimeException("Notification failed: " + e.getMessage());
        }
    }

    private void send(NotificationDTO.Request request) {
        this.http.post().uri("/v1/notification/stable").bodyValue(request).retrieve().bodyToMono(Void.class).block();
    }
}
