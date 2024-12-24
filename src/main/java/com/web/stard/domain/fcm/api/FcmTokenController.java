package com.web.stard.domain.fcm.api;

import com.web.stard.domain.fcm.domain.dto.request.FcmRequestDto;
import com.web.stard.domain.fcm.service.FcmTokenService;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.global.domain.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fcmTokens")
@Tag(name = "FCM 관련 API")
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;

    @PostMapping
    @Operation(summary = "FCM 토큰 등록")
    public ResponseEntity<Void> register(@CurrentMember Member member,
                                         @Valid @RequestBody FcmRequestDto.Register request) {
        fcmTokenService.register(member, request.fcmToken());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @Operation(summary = "FCM 토큰 삭제")
    public ResponseEntity<Void> remove(@CurrentMember Member member) {
        fcmTokenService.remove(member);
        return ResponseEntity.ok().build();
    }

}
