package com.web.stard.domain.board.global.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
    NONE("없음"),
    HOBBY("취미"),
    STUDY("공부"),
    CHAT("잡담"),
    OTHERS("기타");

    private final String description;
}