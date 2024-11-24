package com.web.stard.domain.report.domain.dto.resquest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReportRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportDto {
        @NotNull(message = "id는 필수입니다.")
        private Long targetId;

        @NotBlank(message = "타입은 필수입니다.")
        @Schema(description = "게시글 타입",
                allowableValues = {"comm", "qna", "study", "studypost", "reply"})
        private String postType;

        @NotBlank(message = "신고 사유는 필수입니다.")
        @Schema(description = "신고 사유",
                allowableValues = {"욕설/비방", "음란물", "도배성 글", "기타(사용자 입력)"})
        private String reportReason;

        @Schema(example = " ", description = "기타 신고 사유")
        private String customReason;
    }
}
