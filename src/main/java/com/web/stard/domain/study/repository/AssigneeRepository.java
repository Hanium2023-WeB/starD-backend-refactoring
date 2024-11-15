package com.web.stard.domain.study.repository;

import com.web.stard.domain.study.domain.entity.Assignee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssigneeRepository extends JpaRepository<Assignee, Long> {
}
