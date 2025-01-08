package com.web.stard.domain.teamBlog.api;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.teamBlog.domain.dto.request.EvaluationRequestDto;
import com.web.stard.domain.teamBlog.domain.dto.response.EvaluationResponseDto;
import com.web.stard.domain.teamBlog.service.EvaluationService;
import com.web.stard.global.domain.CurrentMember;
import com.web.stard.global.exception.ApiErrorCodeExample;
import com.web.stard.global.exception.ApiErrorCodeExamples;
import com.web.stard.global.exception.error.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/studies/{studyId}/evaluations")
@RequiredArgsConstructor
@Tag(name = "studies-evaluations", description = "스터디 팀블로그 - 평가 관련 API")
public class EvaluationController {

    private final EvaluationService evaluationService;

    @Operation(summary = "평가 등록", description = "평가 대상 회원 닉네임을 적어주세요.")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND,
            ErrorCode.STUDY_NOT_COMPLETED,
            ErrorCode.STUDY_EVALUATION_BAD_REQUEST,
            ErrorCode.DUPLICATE_STUDY_EVALUATION_REQUEST
    })
    @PostMapping
    public ResponseEntity<Long> createEvaluation(@CurrentMember Member member,
                                                 @PathVariable(name = "studyId") Long studyId,
                                                 @Valid @RequestBody EvaluationRequestDto.CreateDto requestDto) {
        return ResponseEntity.ok(evaluationService.createEvaluation(studyId, member, requestDto));
    }

    @Operation(summary = "평가 사유 수정")
    @ApiErrorCodeExample(ErrorCode.STUDY_EVALUATION_BAD_REQUEST)
    @PutMapping("/{evaluationId}")
    public ResponseEntity<Long> updateReason(@CurrentMember Member member,
                                             @PathVariable(name = "studyId") Long studyId,
                                             @PathVariable(name = "evaluationId") Long evaluationId,
                                             @Valid @RequestBody EvaluationRequestDto.UpdateDto requestDto) {
        return ResponseEntity.ok(evaluationService.updateReason(studyId, evaluationId, member, requestDto));
    }

    @Operation(summary = "사용자의 스터디원 평가 전체 리스트", description = "사용자가 평가할 수 있거나 평가한 스터디원 전체 리스트입니다.")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND,
            ErrorCode.STUDY_NOT_COMPLETED,
            ErrorCode.STUDY_MEMBER_NOT_FOUND
    })
    @GetMapping("/given")
    public ResponseEntity<List<EvaluationResponseDto.EvaluationDto>> getStudyMembersWithEvaluations (
            @CurrentMember Member member,
            @PathVariable(name = "studyId") Long studyId
    ) {
        return ResponseEntity.ok(evaluationService.getStudyMembersWithEvaluations(studyId, member, "given"));
    }

    @Operation(summary = "사용자가 받은 스터디원 평가 전체 리스트", description = "다른 사용자로부터 받거나 받을 리스트입니다.")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND,
            ErrorCode.STUDY_NOT_COMPLETED,
            ErrorCode.STUDY_MEMBER_NOT_FOUND
    })
    @GetMapping("/received")
    public ResponseEntity<List<EvaluationResponseDto.EvaluationDto>> getMemberReceivedEvaluations (
            @CurrentMember Member member,
            @PathVariable(name = "studyId") Long studyId
    ) {
        return ResponseEntity.ok(evaluationService.getStudyMembersWithEvaluations(studyId, member, "received"));
    }
}
