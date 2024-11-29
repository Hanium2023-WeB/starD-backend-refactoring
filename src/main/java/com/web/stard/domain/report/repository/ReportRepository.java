package com.web.stard.domain.report.repository;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.report.domain.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report,Long> {
    boolean existsByTargetIdAndPostTypeAndMember(Long id, PostType postType, Member member);

    @Query(value = "SELECT r.target_id AS targetId, COUNT(r.report_id) AS reportCount, r.post_type AS postType " +
            "FROM report r GROUP BY r.target_id, r.post_type", nativeQuery = true)
    List<Object[]> findReportsWithCountAndPostTypeNative();

    List<Report> findByTargetId(Long targetId);
}
