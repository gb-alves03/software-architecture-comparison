package com.tcc.banking_app_monolith.infra.api.antifraud.dto.response;

public record AntifraudResponseDto(
        String transactionId, boolean isFraudulent, String reason
) {}
