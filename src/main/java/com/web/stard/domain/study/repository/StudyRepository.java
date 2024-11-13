package com.web.stard.domain.study.repository;

import com.web.stard.domain.study.domain.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long> {
}
