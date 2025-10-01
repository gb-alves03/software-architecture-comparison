package com.architecture.account_service.dto;

import java.math.BigDecimal;

public abstract class DebitDTO {
    public static record Input(Long accountId, BigDecimal amount) {
    };
}
