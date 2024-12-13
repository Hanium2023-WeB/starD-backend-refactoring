package com.web.stard.domain.teamBlog.domain.dto.response;

import com.web.stard.domain.teamBlog.domain.entity.Schedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class ScheduleResponseDto {

    @Getter
    @Builder
    public static class ScheduleDto {
        @Schema(description = "일정 고유 id")
        private Long scheduleId;

        @Schema(description = "일정명")
        private String title;

        @Schema(description = "달력 표시 색상")
        private String color;

        @Schema(description = "일정일")
        private LocalDate startDate;

        @Schema(description = "해당 스터디 id")
        private Long studyId;

        public static ScheduleDto from (Schedule schedule) {
            return ScheduleDto.builder()
                    .scheduleId(schedule.getId())
                    .title(schedule.getTitle())
                    .color(schedule.getColor())
                    .startDate(schedule.getStartDate())
                    .studyId(schedule.getStudy().getId())
                    .build();
        }
    }

}
