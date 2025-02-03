package com.web.stard.domain.notification.domain.dto.response;

import com.web.stard.domain.notification.domain.enums.NotificationType;

import java.util.List;

public class NotificationResponse {

    public record InfoResponseDto(
            Long notificationId,
            String title,
            String body,
            boolean read,
            NotificationType type,
            Long targetId
    ) {

    }

    public record InfosResponseDto(
            List<InfoResponseDto> infos,
            int currentPage,
            int totalPages,
            boolean isLast,
            long totalElements
    ) {

    }
}
