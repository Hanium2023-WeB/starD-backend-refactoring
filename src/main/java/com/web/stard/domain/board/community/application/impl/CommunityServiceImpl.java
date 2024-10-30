package com.web.stard.domain.board.community.application.impl;

import com.web.stard.domain.board.community.application.CommunityService;
import com.web.stard.domain.board.community.dto.request.CommRequestDto;
import com.web.stard.domain.board.community.dto.response.CommResponseDto;
import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.domain.enums.Category;
import com.web.stard.domain.board.global.repository.PostRepository;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    // 작성자인지 확인
    private void isPostAuthor(Member member, Post post) {
        if (!member.getId().equals(post.getMember().getId())) {
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }
    }

    /**
     * 커뮤니티 게시글 생성
     *
     * @param requestDto     title, content, category
     *                       제목     내용      카테고리
     * @return CommPostDto  commPostId, title, content, category, hit, writer, updatedAt
     *                       공지 id    제목      내용     카테고리  조회수 작성자     수정일시
     */
    @Override
    public CommResponseDto.CommPostDto createCommPost(CommRequestDto.CreateCommPostDto requestDto) {
        // 회원 정보 반환
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.save(requestDto.toEntity(member));

        return CommResponseDto.CommPostDto.from(post);
    }

    /**
     * 커뮤니티 게시글 수정
     *
     * @param commPostId      수정할 게시글의 id
     * @param requestDto     title, content, category
     *                       제목     내용      카테고리
     * @return CommPostDto  commPostId, title, content, category, hit, writer, updatedAt
     *                       공지 id    제목      내용     카테고리  조회수 작성자     수정일시
     */
    @Transactional
    @Override
    public CommResponseDto.CommPostDto updateCommPost(Long commPostId, CommRequestDto.CreateCommPostDto requestDto) {
        // 회원 정보 반환
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(commPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        isPostAuthor(member, post);

        post.updateComm(requestDto.getTitle(), requestDto.getContent(), Category.find(requestDto.getCategory()));
        Post updatedPost = postRepository.save(post);

        return CommResponseDto.CommPostDto.from(updatedPost);
    }
}
