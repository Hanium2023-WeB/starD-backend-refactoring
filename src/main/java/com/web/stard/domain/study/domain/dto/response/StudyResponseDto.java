package com.web.stard.domain.study.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.domain.enums.InterestField;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyApplicant;
import com.web.stard.domain.study.domain.entity.Tag;
import com.web.stard.domain.study.domain.enums.ActivityType;
import com.web.stard.domain.study.domain.enums.ApplicationStatus;
import com.web.stard.domain.study.domain.enums.ProgressType;
import com.web.stard.domain.study.domain.enums.RecruitmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class StudyResponseDto {

    @Getter
    @NoArgsConstructor
    public static class StudyInfo {

        @Schema(description = "스터디 게시글 아이디")
        private Long studyId;

        @Schema(description = "스터디 게시글 작성자 닉네임")
        private String nickname;

        @Schema(description = "스터디 게시글 작성자 프로필 url")
        private String imgUrl;

        @Schema(description = "스터디 게시글 제목")
        private String title;

        @Schema(description = "스터디 게시글 조회수")
        private Long hit;

        @Schema(description = "스터디 게시글 태그")
        private String tagText;

        @Schema(description = "스터디 활동 타입")
        private ActivityType activityType;

        @Schema(description = "스터디 모집 상황")
        private RecruitmentType recruitmentType;

        @Schema(description = "스터디 활동 시작일")
        private LocalDate activityStart;

        @Schema(description = "스터디 활동 마감일")
        private LocalDate activityDeadline;

        @Schema(description = "스터디 모집 마감일")
        private LocalDate recruitmentDeadline;

        @Schema(description = "스터디 활동 시")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String city;

        @Schema(description = "스터디 활동 구")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String district;

        private int scrapCount;

        @Schema(description = "스터디 분야")
        private InterestField field;

        @Schema(description = "스크랩 여부")
        private boolean isScrapped;

        public void updateScarpStatus(boolean isScrapped) {
            this.isScrapped = isScrapped;
        }

        public void updateScarpCount(int scrapCount) {
            this.scrapCount = scrapCount;
        }
    }

    @Getter
    @Builder
    public static class StudyRecruitListDto {
        private List<StudyResponseDto.DetailInfo> studyRecruitPosts;
        @Schema(description = "현재 페이지")
        private int currentPage;
        @Schema(description = "전체 페이지 수")
        private int totalPages;
        @Schema(description = "마지막 페이지 여부")
        private boolean isLast;

        public static StudyRecruitListDto of(Page<Study> studyPosts, List<StudyResponseDto.DetailInfo> detailInfos) {
            return StudyRecruitListDto.builder()
                    .studyRecruitPosts(detailInfos)
                    .currentPage(studyPosts.getNumber() + 1)
                    .totalPages(studyPosts.getTotalPages())
                    .isLast(studyPosts.isLast())
                    .build();
        }
    }

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

        @Schema(description = "스터디 분야")
        private String field;

        public static DetailInfo toDto(Study study, Member member, int scrapCount) {
            return DetailInfo.builder()
                    .scrapCount(scrapCount)
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
                    .recruitmentDeadline(study.getRecruitmentDeadline())
                    .city(study.getCity())
                    .district(study.getDistrict())
                    .isAuthor(study.getMember().equals(member))
                    .field(study.getField().getDescription())
                    .build();
        }

    }

    @Getter
    @Builder
    public static class StudyInfoListDto {
        private List<StudyResponseDto.StudyInfo> studyInfos;
        private int currentPage;    // 현재 페이지
        private int totalPages;     // 전체 페이지 수
        private boolean isLast;     // 마지막 페이지 여부

        public static StudyInfoListDto of(Page<StudyResponseDto.StudyInfo> infos) {
            return StudyInfoListDto.builder()
                    .studyInfos(infos.getContent())
                    .currentPage(infos.getNumber() + 1)
                    .totalPages(infos.getTotalPages())
                    .isLast(infos.isLast())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class StudyApplicantInfo {
        @Schema(description = "스터디 참여 id")
        private Long applicantId;

        @Schema(description = "스터디 참여자 닉네임")
        private String nickname;

        @Schema(description = "스터디 지원 동기")
        private String introduce;

        @Schema(description = "스터디 신청 상태")
        private ApplicationStatus status;

        public static StudyApplicantInfo toDto(StudyApplicant studyApplicant) {
            return StudyApplicantInfo.builder()
                    .applicantId(studyApplicant.getId())
                    .introduce(studyApplicant.getIntroduction())
                    .nickname(studyApplicant.getMember().getNickname())
                    .status(studyApplicant.getStatus())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class TagInfoDto {

        @Schema(description = "태그 id")
        private Long tagId;

        @Schema(description = "태그 이름")
        private String tagName;

        public static TagInfoDto toDto(Tag tag) {
            return TagInfoDto.builder()
                    .tagId(tag.getId())
                    .tagName(tag.getName())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class TagInfosDto {
        @Schema(description = "태그 정보")
        private List<TagInfoDto> tags;

        public static TagInfosDto toDto(List<TagInfoDto> tags) {
            return TagInfosDto.builder()
                    .tags(tags)
                    .build();
        }
    }
}
