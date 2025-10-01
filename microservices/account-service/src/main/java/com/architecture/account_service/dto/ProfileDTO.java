package com.architecture.account_service.dto;

import java.math.BigDecimal;

public abstract class ProfileDTO {
    public record Input(Long accountId) {
    };

    public record Output(Long accountId, BigDecimal balance, String name, String email, String phone) {
    };
}
