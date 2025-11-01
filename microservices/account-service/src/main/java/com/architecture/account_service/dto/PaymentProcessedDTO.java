package com.architecture.account_service.dto;

import java.math.BigDecimal;

import com.architecture.account_service.enumeration.TransactionStatus;
import com.architecture.account_service.enumeration.TransactionType;

public class PaymentProcessedDTO {
    public static record Input(Long transactionId, Long accountId, BigDecimal amount, TransactionType type, TransactionStatus status) {}; 
}
