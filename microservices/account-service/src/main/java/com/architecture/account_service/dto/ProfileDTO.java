package com.architecture.account_service.dto;

import java.math.BigDecimal;

public abstract class ProfileDTO {
    public static record Input(Long accountId) {
    };

    public static record Output(Long accountId, BigDecimal balance, String name, String email, String phone) {
    };
}
