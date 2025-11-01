package com.tcc.payment_service.dto;

import java.math.BigDecimal;

import com.tcc.payment_service.enumeration.PaymentType;

public abstract class PaymentDTO {
    public static record Input(Long transactionId, Long accountId, BigDecimal amount, PaymentType type) {
    };
}
