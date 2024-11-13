package com.web.stard.domain.study.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.enums.ActivityType;
import com.web.stard.domain.study.domain.enums.ProgressType;
import com.web.stard.domain.study.domain.enums.RecruitmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudyResponseDto {

    @Builder
    @Getter
    public static class DetailInfo {

        @Schema(description = "스터디 게시글 아이디")
        private Long studyId;

        @Schema(description = "스터디 게시글 작성자 닉네임")
        private String nickname;

        @Schema(description = "스터디 게시글 작성자 프로필 url")
        private String profileImg;

        @Schema(description = "스터디 게시글 제목")
        private String title;

        @Schema(description = "스터디 게시글 내용")
        private String content;

        @Schema(description = "스터디 게시글 조회수")
        private Long hit;

        @Schema(description = "스터디 게시글 태그")
        private String tags;

        @Schema(description = "스터디 모집 인원")
        private int capacity;

        @Schema(description = "스터디 활동 타입")
        private ActivityType activityType;

        @Schema(description = "스터디 모집 상황")
        private RecruitmentType recruitmentType;

        @Schema(description = "스터디 진행 상황")
        private ProgressType progressType;

        @Schema(description = "스터디 게시글 생성일")
        private LocalDateTime createdAt;

        @Schema(description = "스터디 게시글 수정일")
        private LocalDateTime updatedAt;

        @Schema(description = "스터디 활동 시작일")
        private LocalDate activityStart;

        @Schema(description = "스터디 활동 마감일")
        private LocalDate activityDeadline;

        @Schema(description = "스터디 모집 시작일")
        private LocalDate recruitmentStart;

        @Schema(description = "스터디 모집 마감일")
        private LocalDate recruitmentDeadline;

        @Schema(description = "스터디 활동 시")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String city;

        @Schema(description = "스터디 활동 구")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String district;

        private int scrapCount;

        @Schema(description = "스터디 게시글 작성 여부")
        private boolean isAuthor;

        public static DetailInfo toDto(Study study, Member member) {
            return DetailInfo.builder()
                    .studyId(study.getId())
                    .nickname(study.getMember().getNickname())
                    .profileImg(member.getProfile().getImgUrl())
                    .title(study.getTitle())
                    .content(study.getContent())
                    .tags(study.getTagText())
                    .capacity(study.getCapacity())
                    .hit(study.getHit())
                    .activityType(study.getActivityType())
                    .recruitmentType(study.getRecruitmentType())
                    .progressType(study.getProgressType())
                    .createdAt(study.getCreatedAt())
                    .updatedAt(study.getUpdatedAt())
                    .activityStart(study.getActivityStart())
                    .activityDeadline(study.getActivityDeadline())
                    .recruitmentStart(study.getRecruitmentStart())
                    .recruitmentDeadline(study.getRecruitmentDeadline())
                    .city(study.getCity())
                    .district(study.getDistrict())
                    .isAuthor(study.getMember().equals(member))
                    .build();
        }

    }
}
