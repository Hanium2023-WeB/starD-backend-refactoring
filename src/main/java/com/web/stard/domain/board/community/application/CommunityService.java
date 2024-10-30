package com.web.stard.domain.board.community.application;

import com.web.stard.domain.board.community.dto.request.CommRequestDto;
import com.web.stard.domain.board.community.dto.response.CommResponseDto;
import org.springframework.http.ResponseEntity;

public interface CommunityService {
    CommResponseDto.CommPostDto createCommPost(CommRequestDto.CreateCommPostDto requestDto);

    CommResponseDto.CommPostDto updateCommPost(Long commPostId, CommRequestDto.CreateCommPostDto requestDto);

    ResponseEntity<String> deleteCommPost(Long commPostId, Long memberId);

}

