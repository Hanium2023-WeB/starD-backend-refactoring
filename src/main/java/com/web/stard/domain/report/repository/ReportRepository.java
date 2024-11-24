package com.web.stard.domain.report.repository;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.report.domain.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report,Long> {
    boolean existsByTargetIdAndPostTypeAndMember(Long id, PostType postType, Member member);
}
