package com.web.stard.domain.notification.service.impl;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.notification.repository.EmitterRepository;
import com.web.stard.domain.notification.repository.NotificationRepository;
import com.web.stard.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmitterRepository emitterRepository;
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
