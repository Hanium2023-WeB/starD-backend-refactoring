package com.web.stard.domain.study.repository;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
    Optional<StudyMember> findByStudyAndMember_Nickname(Study study, String nickname);

    Optional<StudyMember> findByStudyAndMember(Study study, Member member);

    List<StudyMember> findByMember(Member member);

    boolean existsByStudyAndMember(Study study, Member member);

    List<StudyMember> findByStudy(Study study);

    void deleteByMember(Member member);

    Optional<StudyMember> findStudyMemberByMember(Member member);

    Optional<StudyMember> findByMember_NicknameAndStudyIsNull(String nickname); // 회원 탈퇴 용
}
