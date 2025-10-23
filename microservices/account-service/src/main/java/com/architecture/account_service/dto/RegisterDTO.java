package com.architecture.account_service.dto;

public abstract class RegisterDTO {
    public static record Input(String name, String email, String phone) {
    };

    public static record Output(String accountId) {
    };
}
