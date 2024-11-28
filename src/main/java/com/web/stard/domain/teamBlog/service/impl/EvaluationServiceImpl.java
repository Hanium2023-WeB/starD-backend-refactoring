package com.web.stard.domain.teamBlog.service.impl;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyMember;
import com.web.stard.domain.study.domain.enums.ProgressType;
import com.web.stard.domain.study.repository.StudyMemberRepository;
import com.web.stard.domain.study.service.StudyService;
import com.web.stard.domain.teamBlog.domain.dto.request.EvaluationRequestDto;
import com.web.stard.domain.teamBlog.domain.entity.Evaluation;
import com.web.stard.domain.teamBlog.repository.EvaluationRepository;
import com.web.stard.domain.teamBlog.service.EvaluationService;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final StudyService studyService;
    private final StudyMemberRepository studyMemberRepository;


    // 완료된 스터디인지 확인
    public void isStudyCompleted(Study study) {
        if (study.getProgressType() != ProgressType.COMPLETED) {
            throw new CustomException(ErrorCode.STUDY_NOT_COMPLETED);
        }
    }

    /**
     * 스터디 - 팀원 평가 등록
     *
     * @param studyId 해당 study 고유 id
     * @param member 로그인 회원
     * @param requestDto target 평가 대상 닉네임, starRating 별점, starReason 별점 사유
     *
     */
    @Transactional
    @Override
    public Long createEvaluation(Long studyId, Member member, EvaluationRequestDto.CreateDto requestDto) {
        Study study = studyService.findById(studyId);
        isStudyCompleted(study);

        StudyMember studyMember = studyMemberRepository.findByStudyAndMember(study, member)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_MEMBER_NOT_FOUND));
        StudyMember target = studyMemberRepository.findByStudyAndMember_Nickname(study, requestDto.getTarget())
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_MEMBER_NOT_FOUND));

        if (studyMember.getMember().getId() == target.getMember().getId()) { // 자기자신 평가 X
            throw new CustomException(ErrorCode.STUDY_EVALUATION_BAD_REQUEST);
        }

        Evaluation evaluation = Evaluation.builder()
                .starRating(requestDto.getStarRating())
                .starReason(requestDto.getStarReason())
                .study(study)
                .studyMember(studyMember)
                .target(target)
                .build();

        evaluationRepository.save(evaluation);

        // 신뢰도 변경
        List<Evaluation> evaluations = evaluationRepository.findByTarget(target);
        int evaluatorCount = (evaluations != null) ? evaluations.size() : 0;
        target.getMember().getProfile().updateCredibility(requestDto.getStarRating(), evaluatorCount);

        return evaluation.getId();
    }

    /**
     * 스터디 - 팀원 평가 별점 사유 수정
     *
     * @param studyId 해당 study 고유 id
     * @param evaluationId 해당 평가 고유 id
     * @param member 로그인 회원
     * @param requestDto target 평가 대상 닉네임, starRating 별점, starReason 별점 사유
     *
     */
    @Transactional
    @Override
    public Long updateReason(Long studyId, Long evaluationId, Member member, EvaluationRequestDto.UpdateDto requestDto) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_EVALUATION_BAD_REQUEST));

        if (evaluation.getStudyMember().getMember().getId() != member.getId()) {
            throw new CustomException(ErrorCode.STUDY_EVALUATION_BAD_REQUEST);
        }

        evaluation.updateStarReason(requestDto.getStarReason());

        return evaluation.getId();
    }
}
