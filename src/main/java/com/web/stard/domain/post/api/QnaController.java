package com.web.stard.domain.post.api;

import com.web.stard.domain.post.service.PostService;
import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.post.domain.dto.request.PostRequestDto;
import com.web.stard.domain.post.domain.dto.response.PostResponseDto;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.global.domain.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qnas")
@Tag(name = "qnas", description = "QNA 관련 API")
public class QnaController {

    private final PostService postService;

    @Operation(summary = "qna 등록")
    @PostMapping
    public ResponseEntity<PostResponseDto.PostDto> createQna(@Valid @RequestBody PostRequestDto.CreatePostDto requestDto,
                                                             @CurrentMember Member member) {
        return ResponseEntity.ok(postService.createPost(requestDto, member, PostType.QNA));
    }

    @Operation(summary = "qna 수정")
    @PutMapping("/{qnaId}")
    public ResponseEntity<PostResponseDto.PostDto> updateQna(@PathVariable(name = "qnaId") Long qnaId,
                                                           @Valid @RequestBody PostRequestDto.CreatePostDto requestDto,
                                                           @CurrentMember Member member) {
        return ResponseEntity.ok(postService.updatePost(qnaId, requestDto, member, PostType.QNA));
    }

    @Operation(summary = "qna 삭제")
    @DeleteMapping("/{qnaId}")
    public ResponseEntity<Long> deleteQna(@PathVariable(name = "qnaId") Long qnaId, @CurrentMember Member member) {
        return ResponseEntity.ok(postService.deletePost(qnaId, member, PostType.QNA));
    }

    @Operation(summary = "qna 상세 조회")
    @GetMapping("/{qnaId}")
    public ResponseEntity<PostResponseDto.PostDto> getQnaDetail(@PathVariable(name = "qnaId") Long qnaId, @CurrentMember Member member) {
        return ResponseEntity.ok(postService.getPostDetail(qnaId, member, PostType.QNA));
    }

    @Operation(summary = "qna 검색", description = "키워드로 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<PostResponseDto.PostListDto> searchQna(@RequestParam(name = "keyword") String keyword,
                                                                 @RequestParam(name = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(postService.searchPost(keyword, page, PostType.QNA, null));
    }

}
