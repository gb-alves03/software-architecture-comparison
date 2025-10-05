package com.tcc.banking_app_monolith.app.dto.request;

public record RegisterRequestDto(
        String name, String email, String phone
) {}
