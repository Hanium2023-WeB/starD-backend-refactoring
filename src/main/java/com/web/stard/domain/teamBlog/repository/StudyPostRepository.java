package com.web.stard.domain.teamBlog.repository;

import com.web.stard.domain.teamBlog.domain.entity.StudyPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyPostRepository extends JpaRepository<StudyPost, Long> {
}
