package com.web.stard.domain.teamBlog.domain.dto.request;

import com.web.stard.domain.teamBlog.domain.entity.Schedule;
import com.web.stard.domain.study.domain.entity.Study;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class ScheduleRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "ScheduleCreateDto")
    public static class CreateDto {
        @Schema(description = "일정명")
        @NotBlank(message = "일정명을 입력하세요.")
        @Size(max = 50, message = "최대 {max}자까지 입력 가능합니다.")
        private String title;

        @Schema(example = "#ffffff", description = "달력 표시 색상")
        @NotBlank(message = "달력 표시 색상을 입력하세요.")
        private String color;

        @Schema(description = "일정일")
        @NotNull(message = "일정일을 입력하세요.")
        private LocalDate startDate;

        public Schedule toEntity(Study study) {
            return Schedule.builder()
                    .title(title)
                    .color(color)
                    .startDate(startDate)
                    .study(study)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "ScheduleUpdateDto")
    public static class UpdateDto {
        @Schema(description = "일정명")
        @NotBlank(message = "일정명을 입력하세요.")
        @Size(max = 50, message = "최대 {max}자까지 입력 가능합니다.")
        private String title;

        @Schema(example = "#ffffff", description = "달력 표시 색상")
        @NotBlank(message = "달력 표시 색상을 입력하세요.")
        private String color;
    }

}
