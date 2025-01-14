package com.web.stard.domain.teamBlog.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class EvaluationRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDto {
        @Schema(description = "평가할 회원 닉네임")
        @NotBlank(message = "평가할 회원 닉네임을 입력하세요.")
        private String target;

        @Schema(example = "5.0", description = "별점")
        @NotNull(message = "별점을 입력하세요.")
        private double starRating;

        @Schema(description = "별점 사유")
        @NotBlank(message = "별점 사유를 입력하세요.")
        @Size(max = 100, message = "최대 {max}자까지 입력 가능합니다.")
        private String starReason;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDto {
        @Schema(description = "별점 사유")
        @NotBlank(message = "별점 사유를 입력하세요.")
        @Size(max = 100, message = "최대 {max}자까지 입력 가능합니다.")
        private String starReason;
    }
}
