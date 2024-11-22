package com.web.stard.domain.study.repository;

import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.enums.RecruitmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

    List<Study> findByRecruitmentDeadlineBeforeAndRecruitmentType(LocalDate deadLine, RecruitmentType type);

}
