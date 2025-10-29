package com.tcc.banking_app_monolith.account_management.app.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WithdrawalRequestDto(
        @NotNull(message = "Account ID cannot be null")
        Long accountId,
        @NotNull(message = "Amount cannot be null")
        BigDecimal amount
) {}
