package com.web.stard.domain.study.domain.enums;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum ActivityType {
    ONLINE("온라인"),
    OFFLINE("오프라인"),
    ONLINE_OFFLINE("온/오프라인"),
    UNKNOWN("알 수 없음");

    private final String description;

    ActivityType(String description) {
        this.description = description;
    }

    private static final Map<String, ActivityType> descriptions =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(ActivityType::getDescription, Function.identity())));

    public static ActivityType find(String description) {
        return Optional.ofNullable(descriptions.get(description)).orElse(UNKNOWN);
    }

}
