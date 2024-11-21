package com.web.stard.domain.study.service;

import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.study.domain.dto.response.StudyResponseDto;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyApplicant;

import java.util.List;

public interface StudyService {

    void isStudyInProgress(Study study);

    void isStudyMember(Study study, Member member);

    Study createStudy(Member member, Study study);

    Study findById(Long id);

    StudyResponseDto.DetailInfo findStudyDetailInfo(Long studyId, Member member);

    Study updateStudy(Member member, Study updateStudy, Long studyId);

    void deleteStudy(Member member, Long studyId);

    void registerApplication(Member member, Long studyId, StudyApplicant applicant);

    void selectApplicant(Member member, Long studyId, Long applicationId);

    List<StudyApplicant> getApplicants(Member member, Long studyId);
}
