package com.web.stard.domain.notification.domain.dto.request;

import com.web.stard.domain.notification.domain.enums.NotificationType;


public class NotificationRequest {

    public record SendRequestDto(
            String title,
            String body,
            NotificationType type,
            Long targetId
    ) {

    }
}
