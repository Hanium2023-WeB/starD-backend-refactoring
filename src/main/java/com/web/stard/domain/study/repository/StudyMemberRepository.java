package com.web.stard.domain.study.repository;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
    Optional<StudyMember> findByStudyAndMember_Nickname(Study study, String nickname);

    Optional<StudyMember> findByStudyAndMember(Study study, Member member);

    List<StudyMember> findByMember(Member member);

    boolean existsByStudyAndMember(Study study, Member member);

    int countByMember(Member member);

    List<StudyMember> findByStudyId(Long studyId);

    List<StudyMember> findByStudy(Study study);

    @Query("SELECT sm.member FROM StudyMember sm WHERE sm.study = :study")
    List<Member> findMembersByStudy(@Param("study") Study study);

    void deleteByMember(Member member);

    Optional<StudyMember> findStudyMemberByMemberAndStudyId(Member member, Long studyId);

    Optional<StudyMember> findByMember_NicknameAndStudyIsNull(String nickname); // 회원 탈퇴 용

    @EntityGraph(attributePaths = {"study.member", "study.member.profile"})
    @Query("SELECT s FROM Study s JOIN StudyMember sm ON s = sm.study WHERE sm.member = :member ORDER BY " +
            "CASE s.progressType " +
            "WHEN 'IN_PROGRESS' THEN 1 " +
            "WHEN 'CANCELED' THEN 2 " +
            "WHEN 'COMPLETED' THEN 3 " +
            "END, s.createdAt DESC")
    Page<Study> findStudiesByMemberParticipate(@Param("member") Member member, Pageable pageable);
}
