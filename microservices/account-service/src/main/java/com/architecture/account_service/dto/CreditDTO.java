package com.architecture.account_service.dto;

import java.math.BigDecimal;

public abstract class CreditDTO {
    public record Input(Long accountId, BigDecimal amount) {
    };
}
