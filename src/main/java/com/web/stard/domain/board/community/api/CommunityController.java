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

    @Operation(summary = "커뮤니티 게시글 목록 조회")
    @GetMapping
    public ResponseEntity<CommResponseDto.CommPostListDto> getCommPostList(@RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(communityService.getCommPostList(page));
    }

    @Operation(summary = "커뮤니티 게시글 검색 (카테고리 - 전체)", description = "전체 카테고리 중 키워드로 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<CommResponseDto.CommPostListDto> searchCommPost(@RequestParam(name = "keyword") String keyword,
                                                                          @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(communityService.searchCommPost(keyword, page));
    }

    @Operation(summary = "커뮤니티 게시글 검색 (+ 카테고리)", description = "키워드와 카테고리로 검색합니다.")
    @GetMapping("/search/category")
    public ResponseEntity<CommResponseDto.CommPostListDto> searchCommPostWithCategory(@RequestParam(name = "keyword") String keyword,
                                                                                      @RequestParam(name = "category") String category,
                                                                                      @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(communityService.searchCommPostWithCategory(keyword, category, page));
    }
}
