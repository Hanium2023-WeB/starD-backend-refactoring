package com.web.stard.domain.admin.api;

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
@Tag(name = "faqs", description = "FAQ 관련 API")
public class FaqController {

    private final PostService postService;

    @Operation(summary = "faq 등록")
    @ApiErrorCodeExamples({
            ErrorCode.MEMBER_NOT_FOUND, ErrorCode.PERMISSION_DENIED
    })
    @PostMapping("/faqs")
    public ResponseEntity<PostResponseDto.PostDto> createFaq(@Valid @RequestBody PostRequestDto.CreatePostDto requestDto,
                                                             @CurrentMember Member member) {
        return ResponseEntity.ok(postService.createPost(requestDto, member, PostType.FAQ));
    }

    @Operation(summary = "faq 수정")
    @ApiErrorCodeExamples({
            ErrorCode.POST_NOT_FOUND, ErrorCode.PERMISSION_DENIED, ErrorCode.INVALID_ACCESS
    })
    @PutMapping("/faqs/{faqId}")
    public ResponseEntity<PostResponseDto.PostDto> updateFaq(@PathVariable(name = "faqId") Long faqId,
                                                             @Valid @RequestBody PostRequestDto.CreatePostDto requestDto,
                                                             @CurrentMember Member member) {
        return ResponseEntity.ok(postService.updatePost(faqId, requestDto, member, PostType.FAQ));
    }

    @Operation(summary = "faq 삭제")
    @ApiErrorCodeExamples({
            ErrorCode.POST_NOT_FOUND, ErrorCode.PERMISSION_DENIED
    })
    @DeleteMapping("/faqs/{faqId}")
    public ResponseEntity<Long> deleteFaq(@PathVariable(name = "faqId") Long faqId, @CurrentMember Member member) {
        return ResponseEntity.ok(postService.deletePost(faqId, member, PostType.FAQ));
    }

    @Operation(summary = "faq 상세 조회")
    @ApiErrorCodeExample(ErrorCode.POST_NOT_FOUND)
    @GetMapping("/faqs/{faqId}")
    public ResponseEntity<PostResponseDto.PostDto> getFaqDetail(@PathVariable(name = "faqId") Long faqId, @CurrentMember Member member) {
        return ResponseEntity.ok(postService.getPostDetail(faqId, member, PostType.FAQ));
    }
    @Operation(summary = "faq 검색", description = "키워드로 검색합니다.")
    @GetMapping("/faqs/search")
    public ResponseEntity<PostResponseDto.PostListDto> searchFaq(@RequestParam(name = "keyword") String keyword,
                                                                 @RequestParam(name = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(postService.searchPost(keyword, page, PostType.FAQ, null));
    }

    @Operation(summary = "faq, qna 순 목록 조회", description = "faq, qna 순서로 목록을 조회합니다.")
    @GetMapping("/faqs-and-qnas")
    public ResponseEntity<PostResponseDto.PostListDto> getFaqAndQnaList(@RequestParam(name = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(postService.getAllFaqsAndQnas(page));
    }

    @Operation(summary = "faq, qna 전체 검색", description = "키워드로 검색합니다.faq, qna 순서로 검색 결과를 반환합니다.")
    @GetMapping("/faqs-and-qnas/search")
    public ResponseEntity<PostResponseDto.PostListDto> searchFaqsAndQnas(@RequestParam(name = "keyword") String keyword,
                                                                         @RequestParam(name = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(postService.searchFaqsAndQnas(keyword, page));
    }
}
