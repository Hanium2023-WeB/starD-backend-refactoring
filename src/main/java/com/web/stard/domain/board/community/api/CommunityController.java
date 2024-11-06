package com.web.stard.domain.board.community.api;

import com.web.stard.domain.board.global.application.PostService;
import com.web.stard.domain.board.global.domain.enums.PostType;
import com.web.stard.domain.board.global.dto.request.PostRequestDto;
import com.web.stard.domain.board.global.dto.response.PostResponseDto;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.global.domain.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/communities")
public class CommunityController {

    private final PostService postService;

    @Operation(summary = "커뮤니티 게시글 등록")
    @PostMapping
    public ResponseEntity<PostResponseDto.PostDto> createCommPost(@CurrentMember Member member,
                                                                  @Valid @RequestBody PostRequestDto.CreateCommPostDto requestDto) {
        return ResponseEntity.ok(postService.createCommPost(member, requestDto));
    }

    @Operation(summary = "커뮤니티 게시글 수정")
    @PutMapping("/{commPostId}")
    public ResponseEntity<PostResponseDto.PostDto> updateCommPost(@CurrentMember Member member,
                                                                  @PathVariable(name = "commPostId") Long commPostId,
                                                                  @Valid @RequestBody PostRequestDto.CreateCommPostDto requestDto) {
        return ResponseEntity.ok(postService.updateCommPost(member, commPostId, requestDto));
    }

    @Operation(summary = "커뮤니티 게시글 삭제")
    @DeleteMapping("/{commPostId}")
    public ResponseEntity<Long> deleteCommPost(@CurrentMember Member member,
                                               @PathVariable(name = "commPostId") Long commPostId) {
        return ResponseEntity.ok(postService.deletePost(commPostId, member, PostType.COMM));
    }

    @Operation(summary = "커뮤니티 게시글 상세조회")
    @GetMapping("/{commPostId}")
    public ResponseEntity<PostResponseDto.PostDto> getCommPostDetail(@CurrentMember Member member,
                                                                     @PathVariable(name = "commPostId") Long commPostId) {
        return ResponseEntity.ok(postService.getPostDetail(commPostId, member, PostType.COMM));
    }

    @Operation(summary = "커뮤니티 게시글 목록 조회")
    @GetMapping
    public ResponseEntity<PostResponseDto.PostListDto> getCommPostList(@RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(postService.getPostList(page, PostType.COMM));
    }

    @Operation(summary = "커뮤니티 게시글 목록 조회 (+ 카테고리)")
    @GetMapping("/category")
    public ResponseEntity<PostResponseDto.PostListDto> getCommPostListByCategory(@RequestParam(name = "category") String category,
                                                                                 @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(postService.getCommPostListByCategory(category, page));
    }

    @Operation(summary = "커뮤니티 게시글 검색 (카테고리 - 전체)", description = "전체 카테고리 중 키워드로 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<PostResponseDto.PostListDto> searchCommPost(@RequestParam(name = "keyword") String keyword,
                                                                      @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(postService.searchPost(keyword, page, PostType.COMM));
    }

    @Operation(summary = "커뮤니티 게시글 검색 (+ 카테고리)", description = "키워드와 카테고리로 검색합니다.")
    @GetMapping("/search/category")
    public ResponseEntity<PostResponseDto.PostListDto> searchCommPostWithCategory(@RequestParam(name = "keyword") String keyword,
                                                                                  @RequestParam(name = "category") String category,
                                                                                  @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(postService.searchCommPostWithCategory(keyword, category, page));
    }
}
