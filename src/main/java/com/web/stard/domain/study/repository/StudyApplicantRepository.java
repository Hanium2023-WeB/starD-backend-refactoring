package com.web.stard.domain.study.repository;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyApplicant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyApplicantRepository extends JpaRepository<StudyApplicant, Long> {

    boolean existsByMemberAndStudy(Member member, Study study);

    List<StudyApplicant> findByStudy(Study study);
}
