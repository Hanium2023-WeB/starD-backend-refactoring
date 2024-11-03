package com.web.stard.domain.board.community.application;

import com.web.stard.domain.board.community.dto.request.CommRequestDto;
import com.web.stard.domain.board.community.dto.response.CommResponseDto;
import com.web.stard.domain.member.domain.Member;
import org.springframework.http.ResponseEntity;

public interface CommunityService {
    CommResponseDto.CommPostDto createCommPost(Member member, CommRequestDto.CreateCommPostDto requestDto);

    CommResponseDto.CommPostDto updateCommPost(Member member, Long commPostId, CommRequestDto.CreateCommPostDto requestDto);

    ResponseEntity<String> deleteCommPost(Member member, Long commPostId);

    CommResponseDto.CommPostDto getCommPostDetail(Member member, Long commPostId);

    CommResponseDto.CommPostListDto getCommPostList(int page);

    CommResponseDto.CommPostListDto getCommPostListByCategory(String category, int page);

    CommResponseDto.CommPostListDto searchCommPost(String keyword, int page);

    CommResponseDto.CommPostListDto searchCommPostWithCategory(String keyword, String category, int page);

}

