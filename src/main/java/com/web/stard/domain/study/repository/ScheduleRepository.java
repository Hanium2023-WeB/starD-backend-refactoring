package com.web.stard.domain.study.repository;

import com.web.stard.domain.study.domain.entity.Schedule;
import com.web.stard.domain.study.domain.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByStudyAndStartDateBetween(Study study, LocalDate start, LocalDate end);
}
