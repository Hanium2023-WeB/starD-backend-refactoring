package com.web.stard.domain.board.study.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.web.stard.domain.board.study.domain.Study;
import com.web.stard.domain.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class StudyResponseDto {

    @Getter
    @Builder
    public static class StudyRecruitDto {
        private Long studyId;
        private String recruiter;
        private String profileImg;
        private String title;
        private String content;
        private int hit;
        private int capacity;
        private String tags;
        private String onOff;
        private String recruitStatus;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDate activityStart;
        private LocalDate activityDeadline;
        private LocalDate recruitmentStart;
        private LocalDate recruitmentDeadline;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String city;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String district;
        private int scrapCount;

        public static StudyRecruitDto from (Study study, Member recruiter, int scrapCount) {
            return StudyRecruitDto.builder()
                    .studyId(study.getId())
                    .recruiter(recruiter.getUsername())
                    .profileImg(recruiter.getProfile().getImgUrl())
                    .title(study.getTitle())
                    .content(study.getContent())
                    .hit(study.getHit())
                    .capacity(study.getCapacity())
                    .tags(study.getTags())
                    .onOff(study.getOnOff())
                    .recruitStatus(study.getRecruitStatus().getDescription())
                    .createdAt(study.getCreatedAt())
                    .updatedAt(study.getUpdatedAt())
                    .activityStart(study.getActivityStart())
                    .activityDeadline(study.getActivityDeadline())
                    .recruitmentStart(study.getRecruitmentStart())
                    .recruitmentDeadline(study.getRecruitmentDeadline())
                    .city(study.getCity())
                    .district(study.getDistrict())
                    .scrapCount(scrapCount)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class StudyRecruitListDto {
        private List<StudyRecruitDto> studyRecruitPosts;
        private int currentPage;    // 현재 페이지
        private int totalPages;     // 전체 페이지 수
        private boolean isLast;     // 마지막 페이지 여부

        public static StudyRecruitListDto of (Page<Study> studyPosts, List<StudyRecruitDto> studyPostDtos) {
            return StudyRecruitListDto.builder()
                    .studyRecruitPosts(studyPostDtos)
                    .currentPage(studyPosts.getNumber() + 1)
                    .totalPages(studyPosts.getTotalPages())
                    .isLast(studyPosts.isLast())
                    .build();
        }
    }
}
