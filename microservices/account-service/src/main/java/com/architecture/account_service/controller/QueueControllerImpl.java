package com.architecture.account_service.controller;

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
                // DTO para receber a mensagem de sucesso que veio do Payment Service
                // Criar e chamar o método do Account Service responsável por alterar o estado
                // da transação para SUCCESS
            } catch (Exception e) {
            }
            return null;
        });

        this.queue.consume(Constants.PAYMENT_FAILED_QUEUE, message -> {
            try {
                // DTO para receber a mensagem de falha que veio do Payment Service
                // Criar e chamar o método do Account Service responsável por alterar o estado
                // da transação para FAILED e realizar as ações compensatórias de acordo com o
                // tipo de pagamento que foi realizado
            } catch (Exception e) {
            }
            return null;
        });

    }

}
