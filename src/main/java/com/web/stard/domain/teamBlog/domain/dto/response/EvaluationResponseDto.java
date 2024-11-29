package com.web.stard.domain.teamBlog.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.web.stard.domain.study.domain.entity.StudyMember;
import com.web.stard.domain.teamBlog.domain.entity.Evaluation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class EvaluationResponseDto {

    @Getter
    @Builder
    public static class UserGivenEvaluationDto {
        @Schema(description = "해당 스터디 고유 id")
        private Long studyId;

        @Schema(description = "대상 회원 닉네임")
        private String target;

        @Schema(description = "평가 부여 여부")
        private boolean evaluationStatus;

        @Schema(description = "별점")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Double starRating;

        @Schema(description = "별점 사유")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String starReason;

        public static UserGivenEvaluationDto of (StudyMember target, Evaluation evaluation) {
            return UserGivenEvaluationDto.builder()
                    .studyId(target.getStudy().getId())
                    .target(target.getMember().getNickname())
                    .evaluationStatus(evaluation != null ? true : false)
                    .starRating(evaluation != null ? evaluation.getStarRating() : null)
                    .starReason(evaluation != null ? evaluation.getStarReason() : null)
                    .build();
        }
    }
}
