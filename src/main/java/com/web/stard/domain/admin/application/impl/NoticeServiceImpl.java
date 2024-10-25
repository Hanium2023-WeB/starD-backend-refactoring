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

    // 관리자인지 확인
    private void isAdmin(Member member) {
        if (member.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
    }

    // 작성자인지 확인
    private void isPostAuthor(Member member, Post post) {
        if (!member.getId().equals(post.getMember().getId())) {
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }
    }

    @Override
    public NoticeResponseDto.NoticeDto createNotice(NoticeRequestDto.CreateNoticeDto requestDto) {
        // TODO - 로그인한 멤버 정보로 변경
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 권한 체크
        isAdmin(member);

        // 공지 저장
        Post notice = postRepository.save(requestDto.toEntity(member));

        return NoticeResponseDto.NoticeDto.from(notice);
    }

    @Override
    public NoticeResponseDto.NoticeDto updateNotice(Long noticeId, NoticeRequestDto.CreateNoticeDto requestDto) {
        // TODO - 로그인한 멤버 정보로 변경
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post notice = postRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 권한 체크
        isAdmin(member);

        // 작성자인지 확인
        isPostAuthor(member,notice);

        // 공지 수정
        notice.updatePost(requestDto.getTitle(), requestDto.getContent());
        Post updatedNotice = postRepository.save(notice);

        return NoticeResponseDto.NoticeDto.from(updatedNotice);
    }
}
