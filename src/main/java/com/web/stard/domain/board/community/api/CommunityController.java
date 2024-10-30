package com.web.stard.domain.board.community.api;

import com.web.stard.domain.board.community.application.CommunityService;
import com.web.stard.domain.board.community.dto.request.CommRequestDto;
import com.web.stard.domain.board.community.dto.response.CommResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "커뮤니티 게시글 수정")
    @PutMapping("/{commPostId}")
    public ResponseEntity<CommResponseDto.CommPostDto> updateCommPost(@PathVariable(name = "commPostId") Long commPostId,
                                                                      @Valid @RequestBody CommRequestDto.CreateCommPostDto requestDto) {
        return ResponseEntity.ok(communityService.updateCommPost(commPostId, requestDto));
    }

    @Operation(summary = "커뮤니티 게시글 삭제")
    @DeleteMapping("/{commPostId}")
    public ResponseEntity<String> deleteCommPost(@PathVariable(name = "commPostId") Long commPostId,
                                                 @RequestParam(name = "memberId") Long memberId) {
        return communityService.deleteCommPost(commPostId, memberId);
    }

    @Operation(summary = "커뮤니티 게시글 상세조회")
    @GetMapping("/{commPostId}")
    public ResponseEntity<CommResponseDto.CommPostDto> getCommPostDetail(@PathVariable(name = "commPostId") Long commPostId,
                                                                         @RequestParam(name = "memberId") Long memberId) {
        return ResponseEntity.ok(communityService.getCommPostDetail(commPostId, memberId));
    }
}
