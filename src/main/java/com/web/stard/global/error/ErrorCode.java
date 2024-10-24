package com.web.stard.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "이메일이나 비밀번호 불일치"),
    CONFLICT(HttpStatus.CONFLICT, "중복"),
    MISMATCH_TOKEN(HttpStatus.BAD_REQUEST, "토큰 불일치"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰"),
    INVALID_TOKEN(HttpStatus.FORBIDDEN, "토큰 검증 실패"),

    // S3
    UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 업로드에 실패했습니다."),
    DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 삭제에 실패했습니다."),
    SIZE_MISMATCH(HttpStatus.INTERNAL_SERVER_ERROR, "파일 수와 키 이름 수가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
