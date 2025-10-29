package com.tcc.banking_app_monolith.app.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositRequestDto(
        @NotNull(message = "Account ID cannot be null")
        Long accountId,
        @NotNull(message = "Amount cannot be null")
        BigDecimal amount
) {}
