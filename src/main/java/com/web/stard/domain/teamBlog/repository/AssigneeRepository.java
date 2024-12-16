package com.web.stard.domain.teamBlog.repository;

import com.web.stard.domain.study.domain.entity.StudyMember;
import com.web.stard.domain.teamBlog.domain.entity.Assignee;
import com.web.stard.domain.study.domain.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AssigneeRepository extends JpaRepository<Assignee, Long> {
    List<Assignee> findAllByStudyMemberAndToDoDueDateBetween(StudyMember studyMember, LocalDate start, LocalDate end);

    List<Assignee> findAllByStudyMemberAndToDoStudyAndToDoDueDateBetween(StudyMember member, Study study, LocalDate start, LocalDate end);

    List<Assignee> findAllByStudyMember(StudyMember studyMember);
}
