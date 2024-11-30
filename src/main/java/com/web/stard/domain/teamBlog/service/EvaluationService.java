package com.web.stard.domain.teamBlog.service;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.teamBlog.domain.dto.request.EvaluationRequestDto;
import com.web.stard.domain.teamBlog.domain.dto.response.EvaluationResponseDto;

import java.util.List;

public interface EvaluationService {
    Long createEvaluation(Long studyId, Member member, EvaluationRequestDto.CreateDto requestDto);

    Long updateReason(Long studyId, Long evaluationId, Member member, EvaluationRequestDto.UpdateDto requestDto);

    List<EvaluationResponseDto.EvaluationDto> getStudyMembersWithEvaluations(Long studyId, Member member, String status);
}
