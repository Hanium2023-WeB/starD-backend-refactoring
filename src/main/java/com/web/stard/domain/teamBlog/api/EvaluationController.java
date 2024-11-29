package com.web.stard.domain.teamBlog.api;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.teamBlog.domain.dto.request.EvaluationRequestDto;
import com.web.stard.domain.teamBlog.domain.dto.response.EvaluationResponseDto;
import com.web.stard.domain.teamBlog.service.EvaluationService;
import com.web.stard.global.domain.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/studies/{studyId}/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @Operation(summary = "평가 등록", description = "평가 대상 회원 닉네임을 적어주세요.")
    @PostMapping
    public ResponseEntity<Long> createEvaluation(@CurrentMember Member member,
                                                 @PathVariable(name = "studyId") Long studyId,
                                                 @Valid @RequestBody EvaluationRequestDto.CreateDto requestDto) {
        return ResponseEntity.ok(evaluationService.createEvaluation(studyId, member, requestDto));
    }

    @Operation(summary = "평가 사유 수정")
    @PutMapping("/{evaluationId}")
    public ResponseEntity<Long> updateReason(@CurrentMember Member member,
                                             @PathVariable(name = "studyId") Long studyId,
                                             @PathVariable(name = "evaluationId") Long evaluationId,
                                             @Valid @RequestBody EvaluationRequestDto.UpdateDto requestDto) {
        return ResponseEntity.ok(evaluationService.updateReason(studyId, evaluationId, member, requestDto));
    }

    @Operation(summary = "사용자의 스터디원 평가 전체 리스트")
    @GetMapping
    public ResponseEntity<List<EvaluationResponseDto.UserGivenEvaluationDto>> getMembersWithEvaluations (
            @CurrentMember Member member,
            @PathVariable(name = "studyId") Long studyId
    ) {
        return ResponseEntity.ok(evaluationService.getMembersWithEvaluations(studyId, member));
    }
}
