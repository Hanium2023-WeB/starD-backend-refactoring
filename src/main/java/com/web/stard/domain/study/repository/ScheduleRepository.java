package com.web.stard.domain.study.repository;

import com.web.stard.domain.study.domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
