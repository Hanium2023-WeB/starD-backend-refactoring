package com.web.stard.domain.teamBlog.service.impl;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyMember;
import com.web.stard.domain.study.domain.enums.ProgressType;
import com.web.stard.domain.study.repository.StudyMemberRepository;
import com.web.stard.domain.study.service.StudyService;
import com.web.stard.domain.teamBlog.domain.dto.request.EvaluationRequestDto;
import com.web.stard.domain.teamBlog.domain.dto.response.EvaluationResponseDto;
import com.web.stard.domain.teamBlog.domain.entity.Evaluation;
import com.web.stard.domain.teamBlog.repository.EvaluationRepository;
import com.web.stard.domain.teamBlog.service.EvaluationService;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final StudyService studyService;
    private final StudyMemberRepository studyMemberRepository;


    // 완료된 스터디인지 확인
    private void isStudyCompleted(Study study) {
        if (study.getProgressType() != ProgressType.COMPLETED) {
            throw new CustomException(ErrorCode.STUDY_NOT_COMPLETED);
        }
    }

    private Evaluation findEvaluation(StudyMember studyMember, StudyMember target) {
        Optional<Evaluation> evaluation = evaluationRepository.findByStudyMemberAndTarget(studyMember, target);
        if (evaluation.isPresent()) {
            return evaluation.get();
        } else {
            return null;
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

        if (findEvaluation(studyMember, target) != null) { // 이미 존재하면
            throw new CustomException(ErrorCode.DUPLICATE_STUDY_EVALUATION_REQUEST);
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

    /**
     * 스터디 - 팀원 평가 리스트
     *
     * @param studyId 해당 study 고유 id
     * @param member 로그인 회원
     *
     * @return EvaluationDto 리스트 :
     *      studyId, nickname 상대방 회원 닉네임, evaluationStatus 평가 부여 여부, starRating 별점, starReason 별점 사유
     */
    @Transactional
    @Override
    public List<EvaluationResponseDto.EvaluationDto> getStudyMembersWithEvaluations(Long studyId, Member member, String status) {
        Study study = studyService.findById(studyId);
        isStudyCompleted(study);

        // 스터디원 가져오기
        List<StudyMember> studyMembers = studyMemberRepository.findByStudy(study);

        StudyMember user = studyMemberRepository.findByStudyAndMember(study, member)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_MEMBER_NOT_FOUND));

        List<EvaluationResponseDto.EvaluationDto> evaluationDtos =
                studyMembers.stream().map(studyMember -> {
                    if (studyMember.getMember().getId() != member.getId()) { // 자기자신 제외
                        Evaluation evaluation;
                        if (status.equals("given")) {
                            evaluation = findEvaluation(user, studyMember);
                        } else { // "received"
                            evaluation = findEvaluation(studyMember, user);
                        }
                        return EvaluationResponseDto.EvaluationDto.of(studyMember, evaluation);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        return evaluationDtos;
    }
}
