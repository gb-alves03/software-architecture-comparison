package com.tcc.banking_app_monolith.app.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDto(
        @NotBlank(message = "Name cannot be null")
        String name,
        @NotBlank(message = "E-mail cannot be null")
        String email,
        String phone
) {}
