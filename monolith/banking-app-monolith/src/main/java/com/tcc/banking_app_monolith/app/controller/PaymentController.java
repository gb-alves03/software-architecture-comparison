package com.tcc.banking_app_monolith.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.banking_app_monolith.app.dto.request.PaymentRequestDto;
import com.tcc.banking_app_monolith.app.service.PaymentService;
import com.tcc.banking_app_monolith.infra.queue.PaymentQueue;
import com.tcc.banking_app_monolith.utils.Constants;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentQueue queue;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentController(PaymentService paymentService, PaymentQueue queue) {
        this.paymentService = paymentService;
        this.queue = queue;
    }

    @PostConstruct
    public void init() {
        this.queue.consume(Constants.PAYMENT_PROCESSED_QUEUE.getValue(), message -> {
            try {
                PaymentRequestDto payment = objectMapper.readValue(message, PaymentRequestDto.class);
                this.paymentService.processPayment(payment);
            } catch (Exception e) {
            }
            return null;
        });
    }
}
