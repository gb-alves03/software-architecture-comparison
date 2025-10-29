package com.tcc.banking_app_monolith.account_management.infra.api.notification.dto;

import jakarta.validation.constraints.NotNull;

public record NotificationRequestDto(
        Long accountId,
        String message
) {}
