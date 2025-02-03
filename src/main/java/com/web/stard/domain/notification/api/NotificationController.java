package com.web.stard.domain.notification.api;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.notification.domain.dto.response.NotificationResponse;
import com.web.stard.domain.notification.service.NotificationService;
import com.web.stard.global.domain.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
@Tag(name = "notifications", description = "알림 관련 API")
public class NotificationController {

    private final NotificationService notificationService;


    @Operation(summary = "Sse 구독")
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public ResponseEntity<SseEmitter> subscribe(@CurrentMember Member member,
                                                @RequestParam(value = "lastEventId", required = false, defaultValue = "") String lastEventId) {
        return ResponseEntity.ok().body(notificationService.subscribe(member, lastEventId));
    }

    @Operation(summary = "알림 읽음 처리")
    @PutMapping("/{notificationId}")
    public ResponseEntity<Void> getNotification(@CurrentMember Member member, @PathVariable("notificationId") Long notificationId) {
        notificationService.getNotification(member, notificationId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "알림 목록 조회")
    @GetMapping
    public ResponseEntity<NotificationResponse.InfosResponseDto> getNotifications(@CurrentMember Member member,
                                                                                  @RequestParam(defaultValue = "9", name = "size") int size,
                                                                                  @RequestParam(defaultValue = "1", name = "page") int page) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return ResponseEntity.ok().body(notificationService.getNotifications(member, pageable));
    }

}
