package com.web.stard.domain.study.service;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.study.domain.dto.request.StudyRequestDto;
import com.web.stard.domain.study.domain.dto.response.StudyResponseDto;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyApplicant;
import com.web.stard.domain.study.domain.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    List<StudyResponseDto.StudyApplicantInfo> getApplicants(Member member, Long studyId);

    Page<StudyResponseDto.StudyInfo> searchStudies(Member member, StudyRequestDto.StudySearchFilter filter, Pageable pageable);

    Long openStudy(Member member, Long studyId);

    List<Tag> getHotTagTop5();

    StudyResponseDto.StudyRecruitListDto getMemberOpenStudy(Member member, int page);

    StudyResponseDto.StudyRecruitListDto getMemberApplyStudy(Member member, int page);

    StudyResponseDto.StudyRecruitListDto getMemberParticipateStudy(Member member, int page);

    List<StudyResponseDto.StudyMemberInfo> getStudyMembers(Member member, Long studyId);

    List<StudyResponseDto.StudyFieldInfoDto> getTop5HotStudyFields();

    void agreeToStudyDeletion(Member member, Long studyId);

    List<StudyResponseDto.StudyMemberDeletionInfo> getStudyDeletionConsentStatus(Member member, Long studyId);

    void canceledStudy(Member member, Long studyId);

    StudyResponseDto.StudyApplicantInfo getApplicationInfo(Member member, Long studyId);

    List<StudyResponseDto.DetailInfo> getTeamBlogs(Member member);
}
