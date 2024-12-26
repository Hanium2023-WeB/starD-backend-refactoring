package com.web.stard.domain.notification.service.impl;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.domain.notification.domain.dto.request.NotificationRequest;
import com.web.stard.domain.notification.domain.dto.response.NotificationResponse;
import com.web.stard.domain.notification.domain.entity.Notification;
import com.web.stard.domain.notification.repository.EmitterRepository;
import com.web.stard.domain.notification.repository.NotificationRepository;
import com.web.stard.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;


@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;    // 1 hour

    /**
     * Sse 구독
     *
     * @param member      회원 정보
     * @param lastEventId 미수신한 이벤트 id
     * @return SseEmitter
     */
    @Override
    public SseEmitter subscribe(Member member, String lastEventId) {
        String emitterId = member.getId() + "_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        sendToClient(emitter, emitterId, "EventStream Created. [userId=" + member.getId() + "]");

        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(member.getId());
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    /**
     * 단체 알림 전송
     *
     * @param targets
     * @param request
     */
    public void sendNotis(List<Member> targets, NotificationRequest.SendRequestDto request) {
        targets = memberRepository.findAll();
        targets.forEach(target -> {
            sendNoti(target, request);
        });
    }

    /**
     * 단 건 알림 전송
     *
     * @param target
     * @param request
     */
    private void sendNoti(Member target, NotificationRequest.SendRequestDto request) {
        Notification notification = Notification.builder()
                .title(request.title())
                .body(request.body())
                .receiver(target)
                .targetId(request.targetId()).build();
        notification = save(notification);

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByMemberId(target.getId());

        NotificationResponse.SendResponseDto response = new NotificationResponse.SendResponseDto(notification.getId(),
                notification.getTitle(), notification.getBody(), notification.getRead(), notification.getType(), notification.getTargetId());

        sseEmitters.forEach((key, emitter) -> {
            emitterRepository.saveEventCache(key, emitter);
            sendToClient(emitter, key, response);
        });
    }

    /**
     * 알림 저장
     *
     * @param notification
     * @return Notification
     */
    private Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    /**
     * 이벤트 전달
     *
     * @param emitter
     * @param emitterId
     * @param data
     */
    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .data(data));
        } catch (IOException e) {
            emitterRepository.deleteById(emitterId);
            // TODO 커스텀 에러로 변경 필요
            throw new RuntimeException(e);
        }
    }

}
