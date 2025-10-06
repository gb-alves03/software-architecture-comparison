package com.tcc.notification_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tcc.notification_service.dto.NotificationDTO;
import com.tcc.notification_service.service.NotificationService;

public class NotificationControllerImpl implements NotificationController {

    private final NotificationService notificationService;

    public NotificationControllerImpl(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public ResponseEntity<Void> notify(NotificationDTO.Input input) {
        try {
            this.notificationService.notify(input);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @Override
    public ResponseEntity<Void> unstableNotify(NotificationDTO.Input input) {
        try {
            this.notificationService.unstableNotify(input);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

}
