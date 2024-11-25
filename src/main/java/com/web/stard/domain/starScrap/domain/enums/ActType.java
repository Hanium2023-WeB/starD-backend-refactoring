package com.web.stard.domain.starScrap.domain.enums;

import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActType {
    STAR, SCRAP;

    public static ActType fromString(String type) {
        if (type == null || type.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_POST_TYPE);
        }

        return switch (type.toLowerCase()) {
            case "study" -> SCRAP;
            case "studypost" -> SCRAP;
            case "post" -> STAR;
            default -> throw new CustomException(ErrorCode.INVALID_POST_TYPE);
        };
    }
}
