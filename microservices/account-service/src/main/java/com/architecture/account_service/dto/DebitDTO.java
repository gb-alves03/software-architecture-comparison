package com.architecture.account_service.dto;

import java.math.BigDecimal;

public abstract class DebitDTO {
    public record Input(Long accountId, BigDecimal amount) {
    };
}
