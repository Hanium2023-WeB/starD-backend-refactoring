package com.web.stard.domain.report.domain.dto.response;

import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.report.domain.entity.Report;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

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

    @Getter
    @Builder
    public static class ReportDetailDto {
        private Long targetId;
        private String content;
        private int reportCount;
        private PostType postType;
    }

    @Getter
    @Builder
    public static class ReportListDto {
        private List<ReportDetailDto> reports;
        private int currentPage;    // 현재 페이지
        private int totalPages;     // 전체 페이지 수
        private boolean isLast;     // 마지막 페이지 여부
        private long totalElements;

        public static ReportListDto of(Page<ReportResponseDto.ReportDetailDto> reports) {
            return ReportListDto.builder()
                    .reports(reports.getContent())
                    .currentPage(reports.getNumber() + 1)
                    .totalPages(reports.getTotalPages())
                    .isLast(reports.isLast())
                    .totalElements(reports.getTotalElements())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ReportReasonDto {
        private String reason;
        private Long count;
    }

    @Getter
    @Builder
    public static class ReportReasonListDto {
        private List<ReportReasonDto> reportReasons;
        private List<String> customReasons;
    }

    @Getter
    @Builder
    public static class ReportProcessDto {
        private Long targetId;
        private String message;
    }

    @Getter
    @Builder
    public static class ReportMember {
        private Long memberId;
        private String nickname;
        private Integer reportCount;
        private String profileImg;
    }

    @Getter
    @Builder
    public static class ReportMemberListDto {
        private List<ReportMember> members;
        private int currentPage;
        private int totalPages;
        private boolean isLast;
        private long totalElements;

        public static ReportMemberListDto of(Page<ReportResponseDto.ReportMember> members) {
            return ReportMemberListDto.builder()
                    .members(members.getContent())
                    .currentPage(members.getNumber() + 1)
                    .totalPages(members.getTotalPages())
                    .isLast(members.isLast())
                    .totalElements(members.getTotalElements())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ForceDeleteDto {
        private Long deletedMemberId;
        private String message;
    }
}
