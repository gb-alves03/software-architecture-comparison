package com.tcc.payment_service.utils;

public interface Constants {
    String PAYMENT_FAILED = "Payment failed";
    String AMOUNT_MUST_BE_GREATHER_THAN_ZERO = "Amount must be greater than zero";
    
    String PAYMENT_EXCHANGE = "payment.exchange";
    String PAYMENT_PROCESSED_QUEUE = "payment.queue.processed";
    String PAYMENT_SUCESS_QUEUE = "payment.queue.sucess";
    String PAYMENT_FAILED_QUEUE = "payment.queue.failed";
    
    String PAYMENT_PROCESSED_ROUTING_KEY = "payment.processed";
    String PAYMENT_SUCCESS_ROUTING_KEY = "payment.success";
    String PAYMENT_FAILED_ROUTING_KEY = "payment.failed";
}
