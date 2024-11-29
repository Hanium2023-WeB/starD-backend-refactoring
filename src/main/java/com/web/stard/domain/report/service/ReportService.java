package com.web.stard.domain.report.service;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.report.domain.dto.response.ReportResponseDto;
import com.web.stard.domain.report.domain.dto.resquest.ReportRequestDto;

import java.util.List;

public interface ReportService {
    ReportResponseDto.ReportDto createReport(ReportRequestDto.ReportDto requestDto, Member member);

    ReportResponseDto.ReportListDto getReportList(int page, Member member);

    ReportResponseDto.ReportReasonListDto getReportReasonList(Long targetId, Member member);
}
