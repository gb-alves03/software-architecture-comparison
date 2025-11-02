package com.tcc.banking_app_monolith.utils;

public enum Constants {
    ACCOUNT_NOT_FOUND("Account not found"),
    TRANSACTION_NOT_FOUND("Transaction not found"),
    INSUFFICIENT_BALANCE("Insufficient balance"),
    INSUFFICIENT_LIMIT("Insufficient credit limit"),
    TRANSACTION_FAILED("Transaction failed"),
    TRANSACTION_TYPE_NOT_SUPPORTED("Transaction type not supported"),
    AMOUNT_MUST_BE_GREATER_THAN_ZERO("Amount must be greater than zero"),
    PAYMENT_FAILED("Payment failed"),

    PAYMENT_EXCHANGE("payment.exchange"),
    PAYMENT_PROCESSED_QUEUE("payment.queue.processed"),
    PAYMENT_SUCCESS_QUEUE("payment.queue.success"),
    PAYMENT_FAILED_QUEUE("payment.queue.failed"),

    PAYMENT_PROCESSED_ROUTING_KEY("payment.processed"),
    PAYMENT_SUCCESS_ROUTING_KEY("payment.success"),
    PAYMENT_FAILED_ROUTING_KEY("payment.failed");

    private final String value;

    Constants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
