package com.web.stard.domain.report.domain.dto.response;

import com.web.stard.domain.report.domain.entity.Report;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class ReportResponseDto {

    @Getter
    @Builder
    public static class ReportDto {
        private Long reportId;
        private LocalDateTime createdAt;

        public static ReportResponseDto.ReportDto from(Report report) {
            return ReportDto.builder()
                    .reportId(report.getId())
                    .createdAt(report.getCreatedAt())
                    .build();
        }
    }
}
