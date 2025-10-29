package com.tcc.banking_app_monolith.account_management.infra.api.antifraud.dto.response;

public record AntifraudResponseDto(
        String transactionId, boolean isFraudulent, String reason
) {}
