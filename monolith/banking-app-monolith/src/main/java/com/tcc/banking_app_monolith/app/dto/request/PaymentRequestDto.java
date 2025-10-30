package com.tcc.banking_app_monolith.app.dto.request;

import com.tcc.banking_app_monolith.domain.enums.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequestDto(
        @NotNull(message = "Account ID cannot be null")
        Long accountId,
        @NotNull(message = "Amount cannot be null")
        BigDecimal amount,
        @NotBlank(message = "Payment type cannot be blank")
        PaymentType type
) {
}
