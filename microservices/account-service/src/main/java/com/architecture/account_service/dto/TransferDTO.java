package com.architecture.account_service.dto;

import java.math.BigDecimal;

public abstract class TransferDTO {
    public static record Input(Long from, Long to, BigDecimal amount) {
    };
}
