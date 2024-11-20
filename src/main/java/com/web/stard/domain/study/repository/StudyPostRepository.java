package com.web.stard.domain.study.repository;

import com.web.stard.domain.study.domain.entity.StudyPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyPostRepository extends JpaRepository<StudyPost, Long> {
}
