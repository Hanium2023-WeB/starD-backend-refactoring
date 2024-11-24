package com.web.stard.domain.post.domain.enums;

import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostType {
    COMM, QNA, NOTICE, FAQ, STUDY, REPLY, STUDYPOST;

    public static PostType fromString(String type) {
        if (type == null || type.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_POST_TYPE);
        }

        return switch (type.toLowerCase()) {
            case "study" -> STUDY;
            case "studypost" -> STUDYPOST;
            case "comm" -> COMM;
            case "qna" -> QNA;
            case "reply" -> REPLY;
            default -> throw new CustomException(ErrorCode.INVALID_POST_TYPE);
        };
    }
}
