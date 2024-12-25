package com.web.stard.domain.notification.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {

    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    SseEmitter findById(String emitterId);

    void deleteById(String emitterId);

    Map<String, Object> findAllEventCacheStartWithByMemberId(Long memberId);
}
