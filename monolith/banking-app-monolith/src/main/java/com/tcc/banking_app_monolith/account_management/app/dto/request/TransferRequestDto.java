package com.tcc.banking_app_monolith.account_management.app.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequestDto(
        @NotNull(message = "Origin account ID cannot be null")
        Long from,
        @NotNull(message = "Target account ID cannot be null")
        Long to,
        @NotNull(message = "Amount cannot be null")
        BigDecimal amount
) {}
