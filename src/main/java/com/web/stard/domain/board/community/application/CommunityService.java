package com.web.stard.domain.board.community.application;

import com.web.stard.domain.board.community.dto.request.CommRequestDto;
import com.web.stard.domain.board.community.dto.response.CommResponseDto;

public interface CommunityService {
    CommResponseDto.CommPostDto createCommPost(CommRequestDto.CreateCommPostDto requestDto);


}

