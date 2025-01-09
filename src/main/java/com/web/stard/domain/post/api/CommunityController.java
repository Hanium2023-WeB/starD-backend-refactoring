package com.web.stard.domain.post.api;

import com.web.stard.domain.post.service.PostService;
import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.post.domain.dto.request.PostRequestDto;
import com.web.stard.domain.post.domain.dto.response.PostResponseDto;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.global.domain.CurrentMember;
import com.web.stard.global.exception.ApiErrorCodeExample;
import com.web.stard.global.exception.ApiErrorCodeExamples;
import com.web.stard.global.exception.error.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/communities")
@Tag(name = "communities", description = "커뮤니티 관련 API")
public class CommunityController {

    private final PostService postService;

    @Operation(summary = "커뮤니티 게시글 등록", description = "카테고리로는 '없음', '취미', '공부', '잡담', '기타' 중 하나의 값을 전달해주세요.")
    @ApiErrorCodeExample(ErrorCode.MEMBER_NOT_FOUND)
    @PostMapping
    public ResponseEntity<PostResponseDto.PostDto> createCommPost(@CurrentMember Member member,
                                                                  @Valid @RequestBody PostRequestDto.CreateCommPostDto requestDto) {
        return ResponseEntity.ok(postService.createCommPost(member, requestDto));
    }

    @Operation(summary = "커뮤니티 게시글 수정", description = "카테고리로는 '없음', '취미', '공부', '잡담', '기타' 중 하나의 값을 전달해 주세요.")
    @ApiErrorCodeExamples({
            ErrorCode.POST_NOT_FOUND, ErrorCode.INVALID_ACCESS
    })
    @PutMapping("/{commPostId}")
    public ResponseEntity<PostResponseDto.PostDto> updateCommPost(@CurrentMember Member member,
                                                                  @PathVariable(name = "commPostId") Long commPostId,
                                                                  @Valid @RequestBody PostRequestDto.CreateCommPostDto requestDto) {
        return ResponseEntity.ok(postService.updateCommPost(member, commPostId, requestDto));
    }

    @Operation(summary = "커뮤니티 게시글 삭제")
    @ApiErrorCodeExamples({
            ErrorCode.POST_NOT_FOUND, ErrorCode.INVALID_ACCESS
    })
    @DeleteMapping("/{commPostId}")
    public ResponseEntity<Long> deleteCommPost(@CurrentMember Member member,
                                               @PathVariable(name = "commPostId") Long commPostId) {
        return ResponseEntity.ok(postService.deletePost(commPostId, member, PostType.COMM));
    }

    @Operation(summary = "커뮤니티 게시글 상세조회")
    @ApiErrorCodeExample(ErrorCode.POST_NOT_FOUND)
    @GetMapping("/{commPostId}")
    public ResponseEntity<PostResponseDto.PostDto> getCommPostDetail(@CurrentMember Member member,
                                                                     @PathVariable(name = "commPostId") Long commPostId) {
        return ResponseEntity.ok(postService.getPostDetail(commPostId, member, PostType.COMM));
    }

    @Operation(summary = "커뮤니티 게시글 목록 조회")
    @GetMapping
    public ResponseEntity<PostResponseDto.PostListDto> getCommPostList(@CurrentMember Member member,
                                                                       @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(postService.getPostList(page, PostType.COMM, member));
    }

    @Operation(summary = "커뮤니티 게시글 목록 조회 (+ 카테고리)")
    @GetMapping("/category")
    public ResponseEntity<PostResponseDto.PostListDto> getCommPostListByCategory(@CurrentMember Member member,
                                                                                 @RequestParam(name = "category") String category,
                                                                                 @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(postService.getCommPostListByCategory(category, page, member));
    }

    @Operation(summary = "커뮤니티 게시글 검색 (카테고리 - 전체)", description = "전체 카테고리 중 키워드로 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<PostResponseDto.PostListDto> searchCommPost(@CurrentMember Member member,
                                                                      @RequestParam(name = "keyword") String keyword,
                                                                      @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(postService.searchPost(keyword, page, PostType.COMM, member));
    }

    @Operation(summary = "커뮤니티 게시글 검색 (+ 카테고리)", description = "키워드와 카테고리로 검색합니다.")
    @GetMapping("/search/category")
    public ResponseEntity<PostResponseDto.PostListDto> searchCommPostWithCategory(@CurrentMember Member member,
                                                                                  @RequestParam(name = "keyword") String keyword,
                                                                                  @RequestParam(name = "category") String category,
                                                                                  @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(postService.searchCommPostWithCategory(keyword, category, page, member));
    }
}
