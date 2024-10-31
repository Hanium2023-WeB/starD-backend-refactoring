package com.web.stard.domain.member.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    // description으로 InterestField 반환
    private static final Map<String, InterestField> descriptions =
            Collections.unmodifiableMap(Stream.of(values()) // stream을 1개만 생성
                    .collect(Collectors.toMap(InterestField::getDescription, Function.identity())));

    public static InterestField find(String description) {
        return Optional.ofNullable(descriptions.get(description)).orElse(OTHERS);
    }
}