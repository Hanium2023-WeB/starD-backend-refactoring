package com.web.stard.domain.study.repository;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.domain.enums.InterestField;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.enums.ProgressType;
import com.web.stard.domain.study.domain.enums.RecruitmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

    List<Study> findByRecruitmentDeadlineBeforeAndRecruitmentType(LocalDate deadLine, RecruitmentType type);

    List<Study> findByActivityDeadlineBeforeAndProgressType(LocalDate deadLine, ProgressType type);

    @Query("SELECT s.field FROM Study s " +
            "group by s.field order by count(s.field) desc")
    List<InterestField> getTop5HotStudyFields();

    List<Study> findByMember(Member member);

    @Modifying
    @Query("update Study s set s.hit = s.hit + 1 where s.id = :id")
    void incrementHitById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"member.profile"})
    @Query("SELECT s FROM Study s WHERE s.member = :member ORDER BY " +
            "CASE s.recruitmentType " +
            "WHEN 'RECRUITING' THEN 1 " +
            "WHEN 'COMPLETED' THEN 2 " +
            "WHEN 'UNKNOWN' THEN 3 " +
            "END, s.createdAt DESC")
    Page<Study> findOpenStudiesByMember(@Param("member") Member member, Pageable pageable);

    @Query("SELECT s From Study s join StudyMember sm ON s = sm.study " +
            "WHERE sm.member = :member and s.progressType = :type")
    List<Study> findByOnGoingTeamBlogs(@Param("member")Member member, @Param("type")ProgressType type);
}
