package com.tcc.banking_app_monolith.infra.api.notification.client;

import com.tcc.banking_app_monolith.domain.entity.Payment;
import com.tcc.banking_app_monolith.infra.api.notification.dto.NotificationRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentNotificationClient {

    private final RestClient restClient;

    public PaymentNotificationClient(RestClient restClient,
                                     @Value("${notification.url}") String url) {
        this.restClient = restClient.mutate().baseUrl(url).build();
    }


    public void sendNotification(Payment payment) {
        try {
            NotificationRequestDto requestFrom = new NotificationRequestDto(payment.getAccountId(),
                    "Transaction success: " + payment.getAccountId());

            send(requestFrom);
        } catch (Exception e) {
            throw new RuntimeException("Notification failed: " + e.getMessage());
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
