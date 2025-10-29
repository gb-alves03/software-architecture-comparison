package com.tcc.banking_app_monolith.account_management.app.dto.request;

import com.tcc.banking_app_monolith.account_management.domain.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequestDto(
        @NotNull(message = "Account ID cannot be null")
        Long accountId,
        @NotNull(message = "Amount cannot be null")
        BigDecimal amount,
        @NotBlank(message = "Transaction type cannot be blank")
        TransactionType transactionType
) {}
