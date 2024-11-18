package com.web.stard.domain.study.service.impl;

import com.web.stard.domain.board.global.service.StarScrapService;
import com.web.stard.domain.board.global.domain.enums.ActType;
import com.web.stard.domain.board.global.domain.enums.TableType;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.domain.study.domain.dto.response.StudyResponseDto;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.enums.ProgressType;
import com.web.stard.domain.study.repository.StudyRepository;
import com.web.stard.domain.study.service.StudyService;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {

    private final StarScrapService starScrapService;
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;

    // 진행 중인 스터디인지 확인
    @Override
    public void isStudyInProgress(Study study) {
        if (study.getProgressType() != ProgressType.IN_PROGRESS) {
            throw new CustomException(ErrorCode.STUDY_NOT_IN_PROGRESS);
        }
    }

    // TODO : 등록하는 회원이 스터디 멤버인지 확인
    @Override
    public void isStudyMember(Study study, Member member) {

    }

    /**
     * 스터디 모집 게시글 생성
     *
     * @param member 회원 정보
     * @param study  스터디 모집 정보
     * @return Long
     */
    @Override
    public Study createStudy(Member member, Study study) {
        return studyRepository.save(study);
    }

    @Override
    public Study findById(Long id) {
        return studyRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
    }

    @Override
    @Transactional
    public StudyResponseDto.DetailInfo findStudyDetailInfo(Long studyId, Member member) {
        member = memberRepository.findById(member.getId()).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
        int scrapCount = starScrapService.findStarScrapCount(study.getId(), ActType.SCRAP, TableType.STUDY);
        return StudyResponseDto.DetailInfo.toDto(study, member, scrapCount);
    }
}
