package com.tcc.banking_app_monolith.app.dto.request;

import java.math.BigDecimal;

public record PaymentRequestDto(
        Long accountId, BigDecimal amount
) {}
