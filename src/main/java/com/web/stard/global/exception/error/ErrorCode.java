package com.web.stard.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "이메일이나 비밀번호 불일치"),
    EMAIL_CONFLICT(HttpStatus.CONFLICT, "중복된 이메일입니다."),
    NICKNAME_CONFLICT(HttpStatus.CONFLICT, "중복된 닉네임입니다."),
    MISMATCH_TOKEN(HttpStatus.BAD_REQUEST, "토큰 불일치"),

    //Jwt
    EMPTY_CLAIMS(HttpStatus.BAD_REQUEST, "claims 정보가 없는 토큰"),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "지원하지 않는 토큰"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰"),
    INVALID_TOKEN(HttpStatus.FORBIDDEN, "토큰 검증 실패"),

    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    INVALID_ACCESS(HttpStatus.FORBIDDEN, "수정 또는 삭제 권한이 없습니다."),

    // S3
    UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 업로드에 실패했습니다."),
    DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 삭제에 실패했습니다."),
    SIZE_MISMATCH(HttpStatus.INTERNAL_SERVER_ERROR, "파일 수와 키 이름 수가 일치하지 않습니다."),


    // Email
    INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "인증 코드가 일치하지 않습니다."),
    INVALID_OR_EXPIRED_AUTH_CODE(HttpStatus.NOT_FOUND, "이메일을 잘못 입력했거나 인증 시간이 만료되었습니다."),

    // Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
    INVALID_PAGE(HttpStatus.BAD_REQUEST, "유효하지 않은 페이지입니다."),
    DUPLICATE_STAR_SCRAP_REQUEST(HttpStatus.CONFLICT, "이미 공감 및 스크랩을 요청한 게시물입니다."),

    ;

    private final HttpStatus httpStatus;    // HttpStatus
    private final String message;       // 설명
}
