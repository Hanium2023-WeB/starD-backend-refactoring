package com.web.stard.domain.study.domain.enums;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum ProgressType {
    NOT_STARTED("진행 전"),
    IN_PROGRESS("진행 중"),
    COMPLETED("진행 완료"),
    CANCELED("진행 중단"),
    UNKNOWN("알 수 없음");

    private final String description;

    ProgressType(String description) {
        this.description = description;
    }

    private static final Map<String, ProgressType> descriptions =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(ProgressType::getDescription, Function.identity())));

    public static ProgressType find(String description) {
        return Optional.ofNullable(descriptions.get(description)).orElse(UNKNOWN);
    }


}
