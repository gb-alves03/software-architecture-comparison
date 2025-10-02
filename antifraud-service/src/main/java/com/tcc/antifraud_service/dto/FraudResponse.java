package com.tcc.antifraud_service.dto;

public record FraudResponse(
        String transactionId,
        boolean fraudulent,
        String reason
) {}
