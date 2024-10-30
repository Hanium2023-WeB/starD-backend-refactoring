package com.web.stard.domain.board.community.application.impl;

import com.web.stard.domain.board.community.application.CommunityService;
import com.web.stard.domain.board.community.dto.request.CommRequestDto;
import com.web.stard.domain.board.community.dto.response.CommResponseDto;
import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.repository.PostRepository;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Override
    public CommResponseDto.CommPostDto createCommPost(CommRequestDto.CreateCommPostDto requestDto) {
        // 회원 정보 반환
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.save(requestDto.toEntity(member));

        return CommResponseDto.CommPostDto.from(post);
    }
}
