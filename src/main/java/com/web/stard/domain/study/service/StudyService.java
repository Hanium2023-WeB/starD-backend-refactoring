package com.web.stard.domain.study.service;

import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.study.domain.dto.response.StudyResponseDto;
import com.web.stard.domain.study.domain.entity.Study;

public interface StudyService {

    void isStudyInProgress(Study study);

    void isStudyMember(Study study, Member member);

    Study createStudy(Member member, Study study);

    Study findById(Long id);

    StudyResponseDto.DetailInfo findStudyDetailInfo(Long studyId, Member member);
}
