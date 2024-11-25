package com.web.stard.domain.teamBlog.repository;

import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.teamBlog.domain.entity.StudyPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyPostRepository extends JpaRepository<StudyPost, Long> {
    @EntityGraph(attributePaths = {"studyMember.member.profile"})
    Page<StudyPost> findByStudy(Study study, Pageable pageable);
}
