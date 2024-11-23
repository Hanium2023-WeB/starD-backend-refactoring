package com.web.stard.domain.reply.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportReason {
    ABUSE("욕설/비방"),
    PROMOTION("광고"),
    ADULT("음란물"),
    SPAM("도배성 글"),
    CUSTOM("기타(사용자 입력)");

    private final String description;
}
