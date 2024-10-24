package com.web.stard.domain.member.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InterestField {
    DEVELOPMENT_IT("개발/IT"),
    EMPLOYMENT_CERTIFICATE("취업/자격증"),
    DESIGN("디자인"),
    LANGUAGE("언어"),
    SELF_DEVELOPMENT("자기계발"),
    HOBBY("취미"),
    OTHERS("기타");

    private final String description;
}