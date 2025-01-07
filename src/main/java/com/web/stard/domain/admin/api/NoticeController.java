package com.web.stard.domain.admin.api;

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
@RequestMapping("/notices")
@Tag(name = "notices", description = "공지사항 관련 API")
public class NoticeController {

    private final PostService postService;

    @Operation(summary = "공지사항 등록", description = "관리자만 작성 가능합니다.")
    @PostMapping
    public ResponseEntity<PostResponseDto.PostDto> createNotice(@Valid @RequestBody PostRequestDto.CreatePostDto requestDto,
                                                                @CurrentMember Member member) {
        return ResponseEntity.ok(postService.createPost(requestDto, member, PostType.NOTICE));
    }

    @Operation(summary = "공지사항 수정", description = "관리자만 수정 가능합니다.")
    @PutMapping("/{noticeId}")
    public ResponseEntity<PostResponseDto.PostDto> updateNotice(@PathVariable(name = "noticeId") Long noticeId,
                                                                @Valid @RequestBody PostRequestDto.CreatePostDto requestDto,
                                                                @CurrentMember Member member) {
        return ResponseEntity.ok(postService.updatePost(noticeId, requestDto, member, PostType.NOTICE));
    }

    @Operation(summary = "공지사항 삭제", description = "관리자만 삭제 가능합니다.")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Long> deleteNotice(@PathVariable(name = "noticeId") Long noticeId, @CurrentMember Member member) {
        return ResponseEntity.ok(postService.deletePost(noticeId, member, PostType.NOTICE));
    }

    @Operation(summary = "공지사항 목록 조회")
    @GetMapping
    public ResponseEntity<PostResponseDto.PostListDto> getNoticeList(@RequestParam(name = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(postService.getPostList(page, PostType.NOTICE, null));
    }

    @Operation(summary = "공지사항 상세 조회")
    @GetMapping("/{noticeId}")
    public ResponseEntity<PostResponseDto.PostDto> getNoticeDetail(@PathVariable(name = "noticeId") Long noticeId, @CurrentMember Member member) {
        return ResponseEntity.ok(postService.getPostDetail(noticeId, member, PostType.NOTICE));
    }

    @Operation(summary = "공지사항 검색", description = "키워드로 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<PostResponseDto.PostListDto> searchNotice(@RequestParam(name = "keyword") String keyword,
                                                                    @RequestParam(name = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(postService.searchPost(keyword, page, PostType.NOTICE, null));
    }
}
