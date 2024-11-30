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
    public static class EvaluationDto {
        @Schema(description = "해당 스터디 고유 id")
        private Long studyId;

        @Schema(description = "평가 대상 혹은 평가에 참여한 회원 닉네임")
        private String nickname;

        @Schema(description = "평가 부여 여부")
        private boolean evaluationStatus;

        @Schema(description = "별점")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Double starRating;

        @Schema(description = "별점 사유")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String starReason;

        public static EvaluationDto of (StudyMember studyMember, Evaluation evaluation) {
            return EvaluationDto.builder()
                    .studyId(studyMember.getStudy().getId())
                    .nickname(studyMember.getMember().getNickname())
                    .evaluationStatus(evaluation != null ? true : false)
                    .starRating(evaluation != null ? evaluation.getStarRating() : null)
                    .starReason(evaluation != null ? evaluation.getStarReason() : null)
                    .build();
        }
    }
}
