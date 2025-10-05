package com.tcc.notification_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.tcc.notification_service.dto.NotificationDTO;

public interface NotificationController {
    public ResponseEntity<String> notify(@RequestBody NotificationDTO.Input input);

    public ResponseEntity<String> unstableNotify(@RequestBody NotificationDTO.Input input);
}
