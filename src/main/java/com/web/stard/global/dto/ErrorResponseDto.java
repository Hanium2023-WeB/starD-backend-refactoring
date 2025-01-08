package com.web.stard.global.dto;

import com.web.stard.global.exception.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponseDto {

    private final int status;
    private final String errorCode;
    private final String message;

    public static ErrorResponseDto of (ErrorCode errorCode) {
        return new ErrorResponseDto(errorCode.getHttpStatus().value(), errorCode.toString(), errorCode.getMessage());
    }
}
