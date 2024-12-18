package com.web.stard.domain.study.service.impl;

import com.web.stard.domain.starScrap.repository.StarScrapRepository;
import com.web.stard.domain.starScrap.service.StarScrapService;
import com.web.stard.domain.starScrap.domain.enums.ActType;
import com.web.stard.domain.starScrap.domain.enums.TableType;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.domain.study.domain.dto.request.StudyRequestDto;
import com.web.stard.domain.study.domain.dto.response.StudyResponseDto;
import com.web.stard.domain.study.domain.entity.*;
import com.web.stard.domain.study.domain.enums.ApplicationStatus;
import com.web.stard.domain.study.domain.enums.ProgressType;
import com.web.stard.domain.study.domain.enums.RecruitmentType;
import com.web.stard.domain.study.repository.*;
import com.web.stard.domain.study.service.StudyService;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {

    private final StarScrapService starScrapService;
    private final StudyRepository studyRepository;
    private final StarScrapRepository starScrapRepository;
    private final MemberRepository memberRepository;
    private final StudyTagRepository studyTagRepository;
    private final TagRepository tagRepository;
    private final StudyApplicantRepository studyApplicantRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final CustomStudyRepository studyCustomRepository;

    // 진행 중인 스터디인지 확인
    @Override
    public void isStudyInProgress(Study study) {
        if (study.getProgressType() != ProgressType.IN_PROGRESS) {
            throw new CustomException(ErrorCode.STUDY_NOT_IN_PROGRESS);
        }
    }

    @Override
    public void isStudyMember(Study study, Member member) {
        if (!studyMemberRepository.existsByStudyAndMember(study, member)) {
            throw new CustomException(ErrorCode.STUDY_MEMBER_NOT_FOUND);
        }
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
        Study saveStudy = studyRepository.save(study);
        createStudyTags(saveStudy);
        return saveStudy;
    }

    /**
     * 아이디로 스터디 모집 게시글 찾기
     *
     * @param id 스터디 모집 게시글 아이디
     * @return Study
     */
    @Override
    public Study findById(Long id) {
        return studyRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
    }

    /**
     * 스터디 모집 게시글 상세 조회
     *
     * @param studyId 스터디 모집 게시글 id
     * @param member  회원 정보
     * @return DetailInfo
     */
    @Override
    @Transactional
    public StudyResponseDto.DetailInfo findStudyDetailInfo(Long studyId, Member member) {
        member = memberRepository.findById(member.getId()).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
        int scrapCount = starScrapService.findStarScrapCount(study.getId(), ActType.SCRAP, TableType.STUDY);
        studyRepository.incrementHitById(studyId);
        return StudyResponseDto.DetailInfo.toDto(study, member, scrapCount);
    }

    /**
     * 스터디 모집 게시글 수정
     *
     * @param member      회원 정보
     * @param updateStudy 스터디 모집 게시글 정보
     * @return Study
     */
    @Override
    @Transactional
    public Study updateStudy(Member member, Study updateStudy, Long originStudyId) {
        Study originStudy = findById(originStudyId);
        validateAuthor(member, originStudy.getMember());
        validateProgressType(originStudy);
        if (!originStudy.getTagText().equals(updateStudy.getTagText())) {
            updateStudyTags(originStudy, updateStudy.getTagText());
        }
        originStudy.updateStudy(updateStudy);
        return originStudy;
    }

    /**
     * 스터디 모집 게시글 삭제
     *
     * @param member  로그인한 회원 정보
     * @param studyId 삭제할 게시글 id
     */
    @Transactional
    @Override
    public void deleteStudy(Member member, Long studyId) {
        Study study = findById(studyId);
        validateAuthor(member, study.getMember());
        starScrapService.deletePostStarScraps(studyId, ActType.SCRAP, TableType.STUDY);
        studyRepository.delete(study);
    }

    /**
     * 스터디 참여 신청
     *
     * @param member  로그인한 회원 정보
     * @param studyId 스터디 모집 게시글 id
     */
    @Override
    @Transactional
    public void registerApplication(Member member, Long studyId, StudyApplicant studyApplicant) {
        member = memberRepository.findById(member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Study study = findById(studyId);

        if (studyApplicantRepository.existsByMemberAndStudy(member, study)) {
            throw new CustomException(ErrorCode.STUDY_DUPLICATE_APPLICATION);
        }

        studyApplicantRepository.save(StudyApplicant.builder()
                .member(member)
                .study(study)
                .introduction(studyApplicant.getIntroduction())
                .build());
    }

    /**
     * 스터디 참여자 선택
     *
     * @param member        로그인한 회원 정보
     * @param studyId       스터디 모집 게시글 id
     * @param applicationId 스터디 참여자 id
     */
    @Override
    @Transactional
    public void selectApplicant(Member member, Long studyId, Long applicationId) {
        member = memberRepository.findById(member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Study study = findById(studyId);
        validateAuthor(member, study.getMember());

        StudyApplicant applicant = studyApplicantRepository.findById(applicationId).orElseThrow(()
                -> new CustomException(ErrorCode.STUDY_APPLICATION_NOT_FOUND));

        if (applicant.getStatus().equals(ApplicationStatus.ACCEPTED)) {
            applicant.updateStatus(ApplicationStatus.REJECTED);
        } else {
            applicant.updateStatus(ApplicationStatus.ACCEPTED);
        }

    }

    /**
     * 스터디 신청자 목록 조회
     *
     * @param member  로그인한 회원 정보
     * @param studyId 스터디 모집 게시글 id
     */
    @Override
    @Transactional
    public List<StudyApplicant> getApplicants(Member member, Long studyId) {
        member = memberRepository.findById(member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Study study = findById(studyId);
        validateAuthor(member, study.getMember());

        return studyApplicantRepository.findByStudy(study);
    }

    /**
     * 검색 필터로 스터디 검색
     *
     * @param filter   검색 필터 정보
     * @param pageable 페이징 정보
     * @return
     */
    @Override
    @Transactional
    public Page<StudyResponseDto.StudyInfo> searchStudies(Member member,
                                                          StudyRequestDto.StudySearchFilter filter, Pageable pageable) {

        Page<StudyResponseDto.StudyInfo> studyInfos = studyCustomRepository.searchStudiesWithFilter(filter, pageable);
        studyInfos.forEach(studyInfo -> {
            int scrapCount = starScrapRepository.findAllByActTypeAndTableTypeAndTargetId(ActType.SCRAP, TableType.STUDY, studyInfo.getStudyId()).size();
            studyInfo.updateScarpCount(scrapCount);
        });

        if (!Objects.isNull(member)) {
            List<Long> scraps = starScrapRepository.findStudiesByMember(member.getId()).stream().map(Study::getId).toList();
            studyInfos.forEach(studyInfo -> {
                if (scraps.contains(studyInfo.getStudyId())) {
                    studyInfo.updateScarpStatus(true);
                } else {
                    studyInfo.updateScarpStatus(false);
                }
            });
        }

        return studyInfos;
    }

    /**
     * 스터디 팀블로그 오픈
     *
     * @param member  회원 정보
     * @param studyId 스터디 id
     */
    @Override
    @Transactional
    public Long openStudy(Member member, Long studyId) {
        Study study = findById(studyId);
        validateAuthor(member, study.getMember());

        List<StudyApplicant> applicants =
                studyApplicantRepository.findByStudyAndStatus(study, ApplicationStatus.ACCEPTED);

        if (applicants.size() < 2) {
            throw new CustomException(ErrorCode.STUDY_MINIMUM_MEMBERS_REQUIRED);
        }

        if (applicants.size() > study.getCapacity() - 1) {
            throw new CustomException(ErrorCode.STUDY_MEMBER_LIMIT_EXCEEDED);
        }

        List<StudyMember> members = new ArrayList<>();
        members.add(StudyMember.builder()
                .study(study)
                .member(member).build());

        applicants.forEach(applicant -> {
            StudyMember studyMember = StudyMember.builder()
                    .member(applicant.getMember())
                    .study(study).build();
            members.add(studyMember);
        });

        studyMemberRepository.saveAll(members);
        study.updateRecruitmentType(RecruitmentType.COMPLETED);
        study.updateProcessType(ProgressType.IN_PROGRESS);
        return study.getId();
    }

    /**
     * 주간 인기 태그 Top 5 조회
     *
     * @return List -Tag
     */
    @Override
    @Transactional
    public List<Tag> getHotTagTop5() {
        LocalDateTime startTime = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
        return studyTagRepository.tagsByStudy(startTime);
    }

    /**
     * 스터디 모집 게시글 작성자 확인
     *
     * @param member 로그인한 회원 정보
     * @param author 스터디 모집 게시글 작성자 정보
     */
    private void validateAuthor(Member member, Member author) {
        if (!member.getId().equals(author.getId())) {
            throw new CustomException(ErrorCode.STUDY_FORBIDDEN);
        }
    }

    /**
     * 스터디 진행 상황 확인
     *
     * @param study 스터디 모집 게시글 정보
     */
    private void validateProgressType(Study study) {
        if (!study.getProgressType().equals(ProgressType.NOT_STARTED)) {
            throw new CustomException(ErrorCode.STUDY_NOT_EDITABLE);
        }
    }

    /**
     * 스터디 모집 게시글 태그 수정
     *
     * @param study      스터디 모집 게시글 정보
     * @param newTagText 새로운 태그 문자열
     */
    private void updateStudyTags(Study study, String newTagText) {
        List<Tag> existingStudyTags = studyTagRepository.findByStudy(study);
        List<String> currentTagNames = existingStudyTags.stream().map(studyTag -> studyTag.getName()).toList();

        List<String> newTagNames = Arrays.stream(newTagText.split(",")).map(String::trim)
                .filter(tag -> !tag.isEmpty()).toList();

        List<Tag> removeTags = existingStudyTags.stream()
                .filter(studyTag -> !newTagNames.contains(studyTag.getName()))
                .toList();

        List<String> addTags = newTagNames.stream()
                .filter(tag -> !currentTagNames.contains(tag))
                .toList();

        if (!removeTags.isEmpty()) {
            studyTagRepository.deleteByStudyAndTagIn(study, removeTags);
        }

        if (!addTags.isEmpty()) {
            List<Tag> existingTags = tagRepository.findByNameIn(addTags);
            List<String> existingTagNames = existingTags.stream()
                    .map(Tag::getName).toList();

            List<Tag> newTags = addTags.stream()
                    .filter(tagName -> !existingTagNames.contains(tagName))
                    .map(tagName -> Tag.builder().name(tagName).build())
                    .toList();

            if (!newTags.isEmpty()) {
                List<Tag> tags = tagRepository.saveAll(newTags);
                existingTags.addAll(tags);
            }

            List<StudyTag> newStudyTags = existingTags.stream()
                    .map(tag -> new StudyTag(study, tag))
                    .toList();

            newStudyTags = studyTagRepository.saveAll(newStudyTags);
            study.addTags(newStudyTags);

        }
    }

    /**
     * 스터디 모집 게시글 태그 생성
     *
     * @param study 스터디 모집 게시글 정보
     */
    private void createStudyTags(Study study) {
        List<String> tagTexts = Arrays.stream(study.getTagText().split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty()).toList();

        List<Tag> existingTags = tagRepository.findByNameIn(tagTexts);
        List<StudyTag> studyTags = new ArrayList<>();

        tagTexts.forEach(tagText -> {
            Tag tag = existingTags.stream().filter(existingTag ->
                            existingTag.getName().equals(tagText))
                    .findFirst()
                    .orElseGet(() -> tagRepository.save(Tag.builder().name(tagText).build()));

            StudyTag studyTag = StudyTag.builder()
                    .study(study)
                    .tag(tag).build();
            studyTags.add(studyTag);
        });
        List<StudyTag> savedStudyTags = studyTagRepository.saveAll(studyTags);
        study.addTags(savedStudyTags);
    }
}
