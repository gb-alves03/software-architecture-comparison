package com.tcc.notification_service.dto;

public abstract class NotificationDTO {
    public static record Input(Long accountId, String message) {
    };
}
