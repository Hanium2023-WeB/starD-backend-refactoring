package com.web.stard.domain.report.service;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.report.domain.dto.response.ReportResponseDto;
import com.web.stard.domain.report.domain.dto.resquest.ReportRequestDto;

public interface ReportService {
    ReportResponseDto.ReportDto createReport(ReportRequestDto.ReportDto requestDto, Member member);

    ReportResponseDto.ReportListDto getReportList(int page, Member member);

    ReportResponseDto.ReportReasonListDto getReportReasonList(Long targetId, Member member);

    ReportResponseDto.ReportProcessDto approveReport(Long targetId, String postType, Member member);

    ReportResponseDto.ReportProcessDto rejectReport(Long targetId, String postType, Member member);

    ReportResponseDto.ReportMemberListDto getReportedMemberList(int page, Member member);

    ReportResponseDto.ForceDeleteDto forceDeleteMember(Long memberId, Member member);

    void deleteAllRelatedEntities(Member member);
}
