package com.web.stard.domain.member.service.impl;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.domain.entity.Profile;
import com.web.stard.domain.member.domain.enums.InterestField;
import com.web.stard.domain.member.domain.enums.Role;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.domain.member.service.UnknownMemberServiceImpl;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyMember;
import com.web.stard.domain.study.domain.enums.ActivityType;
import com.web.stard.domain.study.repository.StudyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnknownMemberService implements UnknownMemberServiceImpl {

    private final MemberRepository memberRepository;
    private final StudyMemberRepository studyMemberRepository;

    /**
     * '알수없음' 사용자 생성
     */
    @Override
    @Transactional
    public void createUnknownMemberIfNotExist() {
        Member unknownMember = memberRepository.findByNickname("알수없음").orElseGet(
                () -> {
                    // 존재하지 않으면 새로 생성
                    Profile unknownProfile = Profile.builder()
                            .credibility(0)
                            .build();

                    Member unknownNewMember = Member.builder()
                            .email("unknown@stard.com")
                            .nickname("알수없음")
                            .role(Role.USER)
                            .matchingStudyAllow(false)
                            .reportCount(0)
                            .profile(unknownProfile)
                            .build();

                    return memberRepository.save(unknownNewMember);
                }
        );

        studyMemberRepository.findByMember_NicknameAndStudyIsNull("알수없음").ifPresentOrElse(
                studyMember -> {
                },
                () -> {
                    StudyMember unknownStudyMember = StudyMember.builder()
                            .study(null)
                            .member(unknownMember)
                            .build();

                    studyMemberRepository.save(unknownStudyMember);
                }
        );
    }
}
