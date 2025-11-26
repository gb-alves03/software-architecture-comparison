package com.architecture.account_service.dto;

public abstract class NotificationDTO {
    public static record Request(Long accountId, String message) {
    };
}
