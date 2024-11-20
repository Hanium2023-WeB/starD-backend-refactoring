package com.web.stard.domain.study.domain.enums;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum RecruitmentType {
    RECRUITING("모집 중"),
    COMPLETED("모집 완료"),
    UNKNOWN("알 수 없음");

    private final String description;

    RecruitmentType(String description) {
        this.description = description;
    }

    private static final Map<String, RecruitmentType> descriptions =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(RecruitmentType::getDescription, Function.identity())));

    public static RecruitmentType find(String description) {
        return Optional.ofNullable(descriptions.get(description)).orElse(UNKNOWN);
    }
}
