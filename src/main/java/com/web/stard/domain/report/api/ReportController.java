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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
