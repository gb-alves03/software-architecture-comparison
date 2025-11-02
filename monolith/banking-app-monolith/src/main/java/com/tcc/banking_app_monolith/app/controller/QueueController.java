package com.tcc.banking_app_monolith.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.banking_app_monolith.app.dto.request.PaymentProcessedRequestDto;
import com.tcc.banking_app_monolith.app.service.AccountService;
import com.tcc.banking_app_monolith.infra.queue.AccountQueue;
import com.tcc.banking_app_monolith.utils.Constants;

public class QueueController {

    private final AccountService accountService;
    private final AccountQueue accountQueue;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public QueueController(AccountService accountService, AccountQueue accountQueue) {
        this.accountService = accountService;
        this.accountQueue = accountQueue;
    }

    public void init() {
        this.accountQueue.consume(Constants.PAYMENT_SUCCESS_QUEUE.getValue(), message -> {
            try {
                PaymentProcessedRequestDto request = objectMapper.readValue(message, PaymentProcessedRequestDto.class);
                this.accountService.paymentSuccess(request);
            } catch (Exception e) {
            }
            return null;
        });

        this.accountQueue.consume(Constants.PAYMENT_FAILED_QUEUE.getValue(), message -> {
            try {
                PaymentProcessedRequestDto request = objectMapper.readValue(message, PaymentProcessedRequestDto.class);
                this.accountService.paymentFailed(request);
            } catch (Exception e) {
            }
            return null;
        });
    }
}
