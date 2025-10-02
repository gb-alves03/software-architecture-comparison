package com.tcc.antifraud_service.dto;

import java.math.BigDecimal;

public record FraudRequest(
        String transactionId,
        BigDecimal amount,
        String transactionType,
        String sourceAccount,
        String destinationAccount
) {}
