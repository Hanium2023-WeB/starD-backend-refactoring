package com.web.stard.domain.report.api;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.report.domain.dto.response.ReportResponseDto;
import com.web.stard.domain.report.domain.dto.resquest.ReportRequestDto;
import com.web.stard.domain.report.service.ReportService;
import com.web.stard.global.domain.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "게시글, 댓글 신고", description = "targetId에 신고할 글/댓글 id를 전달해 주세요.\n\n" +
            "postType에 신고할 게시글/댓글 타입을 전달해 주세요. [ ex. comm, qna, study, studypost, reply ]\n\n" +
            "reportReason에 신고 사유를 전달해 주세요. [ ex. '욕설/비방', '음란물', '도배성 글', '기타(사용자 입력)']\n\n" +
            "기타(사용자 입력)을 선택한 경우, customReason에 기타 신고 사유를 전달해 주세요. (나머지는 \"\" 또는 null값 전달)")
    @PostMapping
    public ResponseEntity<ReportResponseDto.ReportDto> createReport(@Valid @RequestBody ReportRequestDto.ReportDto requestDto,
                                                                    @CurrentMember Member member) {
        return ResponseEntity.ok(reportService.createReport(requestDto, member));
    }

    @Operation(summary = "신고 목록 조회")
    @GetMapping
    public ResponseEntity<ReportResponseDto.ReportListDto> getReportList(@RequestParam(name = "page", defaultValue = "1", required = false) int page,
                                                                         @CurrentMember Member member) {
        return ResponseEntity.ok(reportService.getReportList(page, member));
    }

    @Operation(summary = "신고 사유 조회", description = "일반 사유는 reportReasons로, 기타 사유(사용자 입력)는 customReasons로 반환됩니다.")
    @GetMapping("/{targetId}")
    public ResponseEntity<ReportResponseDto.ReportReasonListDto> getReportReasonList(@PathVariable(name = "targetId") Long targetId, @CurrentMember Member member) {
        return ResponseEntity.ok(reportService.getReportReasonList(targetId, member));
    }

    @Operation(summary = "신고 승인")
    @PostMapping("/{targetId}/approve")
    public ResponseEntity<ReportResponseDto.ReportProcessDto> approveReport(@PathVariable(name = "targetId") Long targetId,
                                                                            @RequestParam(name = "postType") String postType, @CurrentMember Member member) {
        return ResponseEntity.ok(reportService.approveReport(targetId, postType, member));
    }

    @Operation(summary = "신고 반려")
    @PostMapping("/{targetId}/reject")
    public ResponseEntity<ReportResponseDto.ReportProcessDto> rejectReport(@PathVariable(name = "targetId") Long targetId,
                                                                           @RequestParam(name = "postType") String postType, @CurrentMember Member member) {
        return ResponseEntity.ok(reportService.rejectReport(targetId, postType, member));
    }

    @Operation(summary = "회원 목록 조회", description = "누적 신고 횟수가 1 이상인 회원 목록을 조회합니다.")
    @GetMapping("/members")
    public ResponseEntity<ReportResponseDto.ReportMemberListDto> getReportedMemberList(@RequestParam(name = "page", defaultValue = "1", required = false) int page,
                                                                                       @CurrentMember Member member) {
        return ResponseEntity.ok(reportService.getReportedMemberList(page, member));
    }
}
