package com.web.stard.domain.notification.service;

import com.web.stard.domain.member.domain.entity.Member;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
    SseEmitter subscribe(Member member, String lastEventId);
}
