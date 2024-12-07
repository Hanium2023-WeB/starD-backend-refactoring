package com.web.stard.domain.teamBlog.repository;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.teamBlog.domain.entity.StudyPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyPostRepository extends JpaRepository<StudyPost, Long> {
    @EntityGraph(attributePaths = {"studyMember.member.profile"})
    Page<StudyPost> findByStudy(Study study, Pageable pageable);

    @EntityGraph(attributePaths = {"studyMember.member.profile"})
    Page<StudyPost> findByStudyAndTitleContainingOrContentContaining(Study study, String keyword, String keyword1, Pageable pageable);

    Page<StudyPost> findByStudyMember_MemberAndStudy(Member member, Study study, Pageable pageable);

    List<StudyPost> findByStudyMember_Member(Member member);

    void deleteAllByStudyMember_Member(Member member);
}
