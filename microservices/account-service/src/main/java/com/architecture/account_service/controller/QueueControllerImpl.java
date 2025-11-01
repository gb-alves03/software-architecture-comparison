package com.architecture.account_service.controller;

import com.architecture.account_service.dto.PaymentProcessedDTO;
import com.architecture.account_service.queue.Queue;
import com.architecture.account_service.service.AccountService;
import com.architecture.account_service.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QueueControllerImpl implements QueueController {
    private final AccountService accountService;
    private final Queue queue;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public QueueControllerImpl(AccountService accountService, Queue queue) {
        this.accountService = accountService;
        this.queue = queue;
    }

    @Override
    public void init() {
        this.queue.consume(Constants.PAYMENT_SUCESS_QUEUE, message -> {
            try {
                PaymentProcessedDTO.Input input = objectMapper.readValue(message, PaymentProcessedDTO.Input.class);
                this.accountService.paymentSuccess(input);
            } catch (Exception e) {
            }
            return null;
        });

        this.queue.consume(Constants.PAYMENT_FAILED_QUEUE, message -> {
            try {
                PaymentProcessedDTO.Input input = objectMapper.readValue(message, PaymentProcessedDTO.Input.class);
                this.accountService.paymentFailed(input);
            } catch (Exception e) {
            }
            return null;
        });

    }

}
