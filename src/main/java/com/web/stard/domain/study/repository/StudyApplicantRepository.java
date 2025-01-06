package com.web.stard.domain.study.repository;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyApplicant;
import com.web.stard.domain.study.domain.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyApplicantRepository extends JpaRepository<StudyApplicant, Long> {

    boolean existsByMemberAndStudy(Member member, Study study);

    List<StudyApplicant> findByStudy(Study study);

    Optional<StudyApplicant> findByMemberAndStudy(Member member, Study study);

    List<StudyApplicant> findByStudyAndStatus(Study study, ApplicationStatus status);

    void deleteByMember(Member member);

    List<StudyApplicant> findByMember(Member member);

    void deleteAllByStudy(Study study);

    Page<StudyApplicant> findByMember(Member member, Pageable pageable);
}
