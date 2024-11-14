package com.web.stard.domain.study.domain.dto;

import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.enums.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class StudyRequestDto {

    public record CreateDto(

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
            @Schema(description = "활동 방식",
                    allowableValues = {"ONLINE", "OFFLINE", "ONLINE_OFFLINE"})
            ActivityType activityType,

            @Schema(description = "스터디 활동 시 (활동 방식이 온라인이라면 작성 X)")
            String city,

            @Schema(description = "스터디 활동 구 (활동 방식이 온라인이라면 작성 X)")
            String district,

            @Schema(description = "스터디 태그 (최대 10개)")
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
            @NotNull(message = "스터디 모집 시작일을 선택하세요.")
            LocalDate recruitmentStart,

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
                    .recruitmentStart(recruitmentStart)
                    .recruitmentDeadline(recruitmentDeadline).build();
        }
    }

}
