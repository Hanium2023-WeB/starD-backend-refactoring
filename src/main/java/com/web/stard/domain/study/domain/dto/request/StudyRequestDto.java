package com.web.stard.domain.study.domain.dto.request;

import com.web.stard.domain.member.domain.enums.InterestField;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyApplicant;
import com.web.stard.domain.study.domain.enums.ActivityType;
import com.web.stard.domain.study.domain.enums.RecruitmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class StudyRequestDto {

    public record Save(

            @Schema(description = "제목")
            @NotBlank(message = "제목을 입력하세요.")
            @Size(max = 200, message = "최대 {max}자까지 입력 가능합니다.")
            String title,

            @Schema(description = "내용")
            @NotBlank(message = "내용을 입력하세요.")
            @Size(max = 1000, message = "최대 {max}자까지 입력 가능합니다.")
            String content,

            @Schema(description = "모집 인원")
            @NotNull(message = "모집 인원을 입력하세요.")
            @Min(value = 3, message = "모집 인원은 최소 {value}명 이상이어야 합니다.")
            Integer capacity,

            @NotNull(message = "스터디 활동 방식을 선택하세요.")
            @Schema(description = "활동 방식")
            ActivityType activityType,

            @Schema(description = "스터디 활동 시 (활동 방식이 온라인이라면 작성 X)")
            String city,

            @Schema(description = "스터디 활동 구 (활동 방식이 온라인이라면 작성 X)")
            String district,

            @Schema(description = "스터디 분야",
                    allowableValues = {"기타", "개발/IT", "취업/자격증", "디자인", "언어", "자기계발", "취미"})
            @NotBlank(message = "스터디 분야를 선택하세요.")
            String field,

            @Schema(description = "스터디 태그 (최대 5개)")
            String tags,

            @Schema(description = "스터디 활동 시작일")
            @NotNull(message = "스터디 활동 시작일을 선택하세요.")
            @Future(message = "스터디 활동 시작일은 오늘 이후여야 합니다.")
            LocalDate activityStart,

            @Schema(description = "스터디 활동 마감일")
            @NotNull(message = "스터디 활동 마감일을 선택하세요.")
            @Future(message = "스터디 활동 마감일은 오늘 이후여야 합니다.")
            LocalDate activityDeadline,

            @Schema(description = "스터디 모집 마감일")
            @NotNull(message = "스터디 모집 마감일을 선택하세요.")
            LocalDate recruitmentDeadline
    ) {

        public Study toEntity() {
            return Study.builder()
                    .title(title)
                    .content(content)
                    .capacity(capacity)
                    .activityType(activityType)
                    .city(city)
                    .tagText(tags)
                    .district(district)
                    .activityStart(activityStart)
                    .activityDeadline(activityDeadline)
                    .recruitmentDeadline(recruitmentDeadline)
                    .field(InterestField.find(field)).build();
        }
    }

    public record ApplyStudy(
            @Schema(description = "지원 동기")
            @NotBlank(message = "지원 동기를 입력하세요.")
            @Size(max = 500, message = "최대 {max}자까지 입력 가능합니다.")
            String introduce
    ) {
        public StudyApplicant toEntity() {
            return StudyApplicant.builder()
                    .introduction(introduce)
                    .build();
        }
    }

    public record StudySearchFilter(

            @Schema(description = "현재 페이지 번호 (1부터 시작)", defaultValue = "1")
            int page,

            @Schema(description = "페이지에 보여줄 데이터 건 수", defaultValue = "9")
            int size,

            @Schema(description = "검색 키워드", defaultValue = "검색 키워드")
            String keyword,

            @Schema(description = "검색 태그 키워드", defaultValue = "취준,IT")
            String tags,

            @Schema(description = "모집 상태", defaultValue = "RECRUITING")
            RecruitmentType recruitmentType,

            @Schema(description = "진행 방식", defaultValue = "ONLINE")
            ActivityType activityType,

            @Schema(description = "활동 시")
            String city,

            @Schema(description = "활동 구")
            String district,

            @Schema(description = "스터디 분야", defaultValue = "OTHERS")
            InterestField field

    ) {
    }

}
