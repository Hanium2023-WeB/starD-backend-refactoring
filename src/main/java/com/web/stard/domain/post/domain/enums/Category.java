package com.web.stard.domain.post.domain.enums;

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
public enum Category {
    NONE("없음"),
    HOBBY("취미"),
    STUDY("공부"),
    CHAT("잡담"),
    OTHERS("기타");

    private final String description;

    // description으로 Category 반환
    private static final Map<String, Category> descriptions =
            Collections.unmodifiableMap(Stream.of(values()) // stream을 1개만 생성
                    .collect(Collectors.toMap(Category::getDescription, Function.identity())));

    public static Category find(String description) {
        return Optional.ofNullable(descriptions.get(description)).orElse(NONE); // 찾는 게 없으면 NONE
    }
}