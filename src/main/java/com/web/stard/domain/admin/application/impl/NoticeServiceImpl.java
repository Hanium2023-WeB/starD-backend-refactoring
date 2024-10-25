package com.web.stard.domain.admin.application.impl;

import com.web.stard.domain.admin.application.NoticeService;
import com.web.stard.domain.admin.dto.request.NoticeRequestDto;
import com.web.stard.domain.admin.dto.response.NoticeResponseDto;
import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.repository.PostRepository;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.domain.enums.Role;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.global.error.CustomException;
import com.web.stard.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    private boolean checkMemberRole(Member member) {
        return member.getRole() == Role.ADMIN;
    }

    @Override
    public NoticeResponseDto.NoticeDto createNotice(NoticeRequestDto.CreateNoticeDto createNoticeDto) {
        // TODO - 로그인한 멤버 정보로 변경
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 권한 체크
        if (!checkMemberRole(member)) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }

        // 공지 저장
        Post notice = postRepository.save(createNoticeDto.toEntity(member));

        return NoticeResponseDto.NoticeDto.from(notice);
    }
}
