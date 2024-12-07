package com.web.stard.domain.member.repository;

import com.web.stard.domain.member.domain.entity.Member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM Member m JOIN FETCH m.profile WHERE m.id = :memberId")
    Member findByIdWithProfile(@Param("memberId") Long memberId);

    Page<Member> findByReportCountGreaterThanEqual(int reportCount, Pageable pageable);

    List<Member> findByReportCountGreaterThanEqual(int reportCount);

    Optional<Member> findByNickname(String nickname);
}
