package com.web.stard.domain.board.study.repository;

import com.web.stard.domain.board.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long> {
}
