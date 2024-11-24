package com.web.stard.domain.report.domain.enums;

import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
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

    public static ReportReason fromString(String reportReason) {
        if (reportReason == null || reportReason.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_REPORT_REASON);
        }

        for (ReportReason reason : values()) {
            if (reason.description.equals(reportReason)) {
                return reason;
            }
        }
        throw new CustomException(ErrorCode.INVALID_REPORT_REASON);
    }
}
