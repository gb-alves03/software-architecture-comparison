package com.tcc.banking_app_monolith.infra.api.antifraud.dto.request;

import java.math.BigDecimal;

public record AntifraudRequestDto(
        String transactionId, BigDecimal amount, String transactionType, String accountId
) {}
