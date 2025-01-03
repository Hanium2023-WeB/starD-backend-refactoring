package com.web.stard.domain.teamBlog.repository;

import com.web.stard.domain.study.domain.entity.StudyMember;
import com.web.stard.domain.teamBlog.domain.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByTarget(StudyMember studyMember);

    Optional<Evaluation> findByStudyMemberAndTarget(StudyMember studyMember, StudyMember target);

    List<Evaluation> findByStudyMember(StudyMember studyMember);
}
