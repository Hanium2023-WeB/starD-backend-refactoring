package com.web.stard.domain.member.service.impl;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.domain.entity.Profile;
import com.web.stard.domain.member.domain.enums.Role;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.domain.member.service.UnknownMemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnknownMemberService implements UnknownMemberServiceImpl {

    private final MemberRepository memberRepository;

    /**
     * '알수없음' 사용자 생성
     */
    @Override
    @Transactional
    public void createUnknownMemberIfNotExist() {
        memberRepository.findByNickname("알수없음").ifPresentOrElse(
                member -> {
                },
                () -> {
                    // 존재하지 않으면 새로 생성
                    Profile unknownProfile = Profile.builder()
                            .credibility(0)
                            .build();

                    Member unknownMember = Member.builder()
                            .email("unknown@stard.com")
                            .nickname("알수없음")
                            .role(Role.USER)
                            .matchingStudyAllow(false)
                            .reportCount(0)
                            .profile(unknownProfile)
                            .build();

                    memberRepository.save(unknownMember);
                }
        );
    }
}
