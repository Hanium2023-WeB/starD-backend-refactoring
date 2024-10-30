package com.web.stard.domain.board.community.api;

import com.web.stard.domain.board.community.application.CommunityService;
import com.web.stard.domain.board.community.dto.request.CommRequestDto;
import com.web.stard.domain.board.community.dto.response.CommResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final CommunityService communityService;

    @Operation(summary = "커뮤니티 게시글 등록")
    @PostMapping
    public ResponseEntity<CommResponseDto.CommPostDto> createCommPost(@Valid @RequestBody CommRequestDto.CreateCommPostDto requestDto) {
        return ResponseEntity.ok(communityService.createCommPost(requestDto));
    }
}
