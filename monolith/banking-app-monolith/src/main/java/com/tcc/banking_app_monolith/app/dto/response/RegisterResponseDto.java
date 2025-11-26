package com.tcc.banking_app_monolith.app.dto.response;

public record RegisterResponseDto(
        Long accountId, String name, String email, String phone, Long ownerId
) {
}
