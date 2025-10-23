package com.tcc.notification_service.notification;

public interface Notification {
    void send(Long accountId, String message);
}
