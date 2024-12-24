package com.web.stard.domain.fcm.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class FcmRequestDto {

    public record Register(
            @Schema(description = "FCM 토큰")
            @NotBlank(message = "FCM 토큰을 입력하세요.")
            String fcmToken
    ) {

    }
}
