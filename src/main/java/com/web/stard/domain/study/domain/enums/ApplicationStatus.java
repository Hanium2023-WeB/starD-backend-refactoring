package com.web.stard.domain.study.domain.enums;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum ApplicationStatus {

    ACCEPTED("수락"),
    REJECTED("거절"),
    PENDING("대기");

    private final String description;

    ApplicationStatus(String description) {
        this.description = description;
    }

    private static final Map<String, ApplicationStatus> descriptions =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(ApplicationStatus::getDescription, Function.identity())));

    public static ApplicationStatus find(String description) {
        return Optional.ofNullable(descriptions.get(description)).get();
    }

}
