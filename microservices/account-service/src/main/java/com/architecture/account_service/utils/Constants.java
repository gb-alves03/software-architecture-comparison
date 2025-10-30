package com.architecture.account_service.utils;

public interface Constants {
    String ACCOUNT_NOT_FOUND = "Account not found";
    String INSUFFICIENT_BALANCE = "Insufficient balance";
    String TRANSACTION_FAILED = "Transaction failed";
    String TRANSACTION_TYPE_NOT_SUPPORTED = "Transaction type not supported";
    String AMOUNT_MUST_BE_GREATHER_THAN_ZERO = "Amount must be greater than zero";

    String PAYMENT_EXCHANGE = "payment.exchange";
    String PAYMENT_PROCESSED_QUEUE = "payment.queue.processed";
    String PAYMENT_SUCESS_QUEUE = "payment.queue.sucess";
    String PAYMENT_FAILED_QUEUE = "payment.queue.failed";
    
    String PAYMENT_PROCESSED_ROUTING_KEY = "payment.processed";
    String PAYMENT_SUCCESS_ROUTING_KEY = "payment.success";
    String PAYMENT_FAILED_ROUTING_KEY = "payment.failed";
}
