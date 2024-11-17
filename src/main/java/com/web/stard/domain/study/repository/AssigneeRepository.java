package com.web.stard.domain.study.repository;

import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.study.domain.entity.Assignee;
import com.web.stard.domain.study.domain.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AssigneeRepository extends JpaRepository<Assignee, Long> {
    List<Assignee> findAllByMemberAndToDoDueDateBetween(Member member, LocalDate start, LocalDate end);
    List<Assignee> findAllByMemberAndToDoStudyAndToDoDueDateBetween(Member member, Study study, LocalDate start, LocalDate end);
}
