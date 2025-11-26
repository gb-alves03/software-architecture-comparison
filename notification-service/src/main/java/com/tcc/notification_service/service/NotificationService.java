package com.tcc.notification_service.service;

import com.tcc.notification_service.dto.NotificationDTO;

public interface NotificationService {
    public void notify(NotificationDTO.Input input);

    public void unstableNotify(NotificationDTO.Input input);
}
