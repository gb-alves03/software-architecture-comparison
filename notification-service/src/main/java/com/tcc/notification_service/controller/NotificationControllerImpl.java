package com.tcc.notification_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.notification_service.dto.NotificationDTO;
import com.tcc.notification_service.service.NotificationService;

@RestController
@RequestMapping("/v1/notification")
public class NotificationControllerImpl implements NotificationController {

    private final NotificationService notificationService;

    public NotificationControllerImpl(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/stable")
    @Override
    public ResponseEntity<Void> notify(NotificationDTO.Input input) {
        try {
            this.notificationService.notify(input);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @PostMapping("/unstable")
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
