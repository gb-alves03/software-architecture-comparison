package com.tcc.banking_app_monolith.account_management.app.dto.response;

public record RegisterResponseDto(
        Long accountId, String name, String email, String phone, Long ownerId
) {
}
