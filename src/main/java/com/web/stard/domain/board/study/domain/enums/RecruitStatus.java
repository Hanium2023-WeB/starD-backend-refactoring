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
public enum RecruitStatus {
    RECRUITING("모집 중"),
    RECRUITMENT_COMPLETE("모집 완료"),
    RECRUITMENT_DISCONTINUE("모집 중단");

    private final String description;

    // description으로 RecruitStatus 반환
    private static final Map<String, RecruitStatus> descriptions =
            Collections.unmodifiableMap(Stream.of(values()) // stream을 1개만 생성
                    .collect(Collectors.toMap(RecruitStatus::getDescription, Function.identity())));

    public static RecruitStatus find(String description) {
        return Optional.ofNullable(descriptions.get(description)).orElse(RECRUITMENT_DISCONTINUE);
    }
}
