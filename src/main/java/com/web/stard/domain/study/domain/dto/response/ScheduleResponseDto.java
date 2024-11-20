package com.web.stard.domain.study.domain.dto.response;

import com.web.stard.domain.study.domain.entity.Schedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class ScheduleResponseDto {

    @Getter
    @Builder
    public static class ScheduleDto {
        private Long scheduleId;
        private String title;
        private String color;
        private LocalDate startDate;
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
