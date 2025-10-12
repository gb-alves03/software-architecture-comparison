package com.tcc.payment_service.controller;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.payment_service.dto.PaymentDTO;
import com.tcc.payment_service.queue.Queue;
import com.tcc.payment_service.service.PaymentServiceImpl;

import jakarta.annotation.PostConstruct;

@Component
public class PaymentController {
    private final PaymentServiceImpl paymentService;
    private final Queue queue;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentController(PaymentServiceImpl paymentService, Queue queue) {
        this.paymentService = paymentService;
        this.queue = queue;
    }

    @PostConstruct
    public void init() {
        this.queue.consume("payment.done.queue", message -> {
            try {
                PaymentDTO.Input payment = objectMapper.readValue(message, PaymentDTO.Input.class);
                this.paymentService.processPayment(payment);
            } catch (Exception e) {
                // Exemplo de ação compensatória
                // queue.publish("payment.events", "payment.failed", message);
            }
            return null;
        });
    }
}
