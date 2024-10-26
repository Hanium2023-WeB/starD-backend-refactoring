package com.web.stard.global.exception.error;

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

    //Jwt
    EMPTY_CLAIMS(HttpStatus.BAD_REQUEST, "claims 정보가 없는 토큰"),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "지원하지 않는 토큰"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰"),
    INVALID_TOKEN(HttpStatus.FORBIDDEN, "토큰 검증 실패"),

    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    INVALID_ACCESS(HttpStatus.FORBIDDEN, "유효하지 않은 접근입니다."),

    // S3
    UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 업로드에 실패했습니다."),
    DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 삭제에 실패했습니다."),
    SIZE_MISMATCH(HttpStatus.INTERNAL_SERVER_ERROR, "파일 수와 키 이름 수가 일치하지 않습니다."),


    // Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다.")

    ;

    private final HttpStatus httpStatus;    // HttpStatus
    private final String message;       // 설명
}
