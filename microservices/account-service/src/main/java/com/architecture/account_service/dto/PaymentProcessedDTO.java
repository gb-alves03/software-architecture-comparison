package com.architecture.account_service.dto;

import com.architecture.account_service.enumeration.TransactionStatus;

public class PaymentProcessedDTO {
    public static record Input(Long transactionId, TransactionStatus status) {}; 
}
