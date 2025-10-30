package com.architecture.account_service.utils;

public interface Constants {
    String ACCOUNT_NOT_FOUND = "Account not found";
    String INSUFFICIENT_BALANCE = "Insufficient balance";
    String TRANSACTION_FAILED = "Transaction failed";
    String TRANSACTION_TYPE_NOT_SUPPORTED = "Transaction type not supported";
    String AMOUNT_MUST_BE_GREATHER_THAN_ZERO = "Amount must be greater than zero";

    String PAYMENT_EXCHANGE = "payment.exchange";
    String PAYMENT_QUEUE = "payment.queue";
    String PAYMENT_SUCESS = "payment.sucess";
    String PAYMENT_FAILED = "payment.failed";
}
