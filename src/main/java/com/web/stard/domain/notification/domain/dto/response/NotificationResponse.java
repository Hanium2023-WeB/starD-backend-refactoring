package com.web.stard.domain.notification.domain.dto.response;

import com.web.stard.domain.notification.domain.enums.NotificationType;

public class NotificationResponse {

    public  record SendResponseDto (
            Long NotificationId,
            String title,
            String body,
            boolean read,
            NotificationType type,
            Long targetId
    ) {

    }
}
