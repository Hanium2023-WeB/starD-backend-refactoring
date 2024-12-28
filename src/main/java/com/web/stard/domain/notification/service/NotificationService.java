package com.web.stard.domain.notification.service;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.notification.domain.dto.request.NotificationRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotificationService {
    SseEmitter subscribe(Member member, String lastEventId);
    void sendNotis(List<Member> targets, NotificationRequest.SendRequestDto request);
}
