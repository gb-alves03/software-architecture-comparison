package com.architecture.account_service.dto;

import java.math.BigDecimal;

import com.architecture.account_service.enumeration.TransactionType;

public abstract class PaymentDTO {
    public static record Input(Long from, Long to, BigDecimal amount, TransactionType type) {
    };
}
