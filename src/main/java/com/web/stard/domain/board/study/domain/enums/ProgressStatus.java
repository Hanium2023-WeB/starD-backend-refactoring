package com.web.stard.domain.board.study.domain.enums;

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
public enum ProgressStatus {
    BEFORE_PROCEEDING("진행 전"),
    IN_PROGRESS("진행 중"), // WIP (Work In Progress)
    WRAP_UP("진행 완료"),
    DISCONTINUE("진행 중단");

    private final String description;

    // description으로 ProgressStatus 반환
    private static final Map<String, ProgressStatus> descriptions =
            Collections.unmodifiableMap(Stream.of(values()) // stream을 1개만 생성
                    .collect(Collectors.toMap(ProgressStatus::getDescription, Function.identity())));

    public static ProgressStatus find(String description) {
        return Optional.ofNullable(descriptions.get(description)).orElse(BEFORE_PROCEEDING);
    }
}
