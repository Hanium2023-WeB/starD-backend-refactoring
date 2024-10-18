package com.web.stard.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "이메일이나 비밀번호 불일치"),
    CONFLICT(HttpStatus.CONFLICT, "중복"),
    MISMATCH_TOKEN(HttpStatus.BAD_REQUEST, "토큰 불일치"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰"),
    INVALID_TOKEN(HttpStatus.FORBIDDEN, "토큰 검증 실패");

    private final HttpStatus httpStatus;
    private final String message;
}
