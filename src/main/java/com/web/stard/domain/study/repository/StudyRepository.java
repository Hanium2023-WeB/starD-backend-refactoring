package com.web.stard.domain.study.repository;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.enums.RecruitmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

    List<Study> findByRecruitmentDeadlineBeforeAndRecruitmentType(LocalDate deadLine, RecruitmentType type);

    List<Study> findByMember(Member member);

    @Modifying
    @Query("update Study s set s.hit = s.hit + 1 where s.id = :id")
    void incrementHitById(@Param("id") Long id);
}
