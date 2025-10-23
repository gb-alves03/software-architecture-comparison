package com.tcc.notification_service.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.tcc.notification_service.dto.NotificationDTO;
import com.tcc.notification_service.notification.Notification;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final Notification notification;

    public NotificationServiceImpl(Notification notification) {
        this.notification = notification;
    }

    @Override
    public void notify(NotificationDTO.Input input) {
        this.notification.send(input.accountId(), input.message());
    }

    @Override
    public void unstableNotify(NotificationDTO.Input input) {
        Random random = new Random();
        if (random.nextDouble() < 0.3) {
            throw new RuntimeException("Simulated Fail");
        }
        this.notification.send(input.accountId(), input.message());
    }
}
