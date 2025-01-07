package com.web.stard.domain.reply.api;

import com.web.stard.domain.reply.service.ReplyService;
import com.web.stard.domain.reply.domain.dto.request.ReplyRequestDto;
import com.web.stard.domain.reply.domain.dto.response.ReplyResponseDto;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.global.domain.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/replies")
@Tag(name = "replies", description = "댓글 관련 API")
public class ReplyController {

    private final ReplyService replyService;

    @Operation(summary = "댓글 등록", description = "댓글을 작성할 게시글 id를 targetId에 전달해 주세요.\n\n" +
            "작성할 댓글의 게시글 type을 전달해 주세요. [ ex. study / studypost / comm / qna ]")
    @PostMapping("/{targetId}")
    public ResponseEntity<ReplyResponseDto.ReplyDto> createReply(@PathVariable(name = "targetId") Long targetId,
                                                                 @Valid @RequestBody ReplyRequestDto.CreateReplyDto requestDto,
                                                                 @CurrentMember Member member) {
        return ResponseEntity.ok(replyService.createReply(targetId, requestDto, member));
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/{replyId}")
    public ResponseEntity<ReplyResponseDto.ReplyDto> updateReply(@PathVariable(name = "replyId") Long replyId,
                                                                 @Valid @RequestBody ReplyRequestDto.UpdateReplyDto requestDto,
                                                                 @CurrentMember Member member) {
        return ResponseEntity.ok(replyService.updateReply(replyId, requestDto, member));
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{replyId}")
    public ResponseEntity<Long> deleteReply(@PathVariable(name = "replyId") Long replyId, @CurrentMember Member member) {
        return ResponseEntity.ok(replyService.deleteReply(replyId, member));
    }

    @Operation(summary = "댓글 목록 조회", description = "조회할 댓글들의 게시글 type을 전달해 주세요. [ ex. study / studypost / comm / qna ]")
    @GetMapping("/{targetId}")
    public ResponseEntity<ReplyResponseDto.ReplyListDto> getReplyList(@PathVariable(name = "targetId") Long targetId,
                                                                      @RequestParam(name = "type") String type,
                                                                      @RequestParam(name = "page", defaultValue = "1", required = false) int page,
                                                                      @CurrentMember Member member) {
        return ResponseEntity.ok(replyService.getReplyList(targetId, type, page, member));

    }

    @Operation(summary = "댓글 id로 부모 게시글 id 조회", description = "신고 내역에서 대상 글을 조회할 때 사용합니다.")
    @GetMapping("/{replyId}/parent")
    public ResponseEntity<ReplyResponseDto.ReplyParentDto> getReplyParent(@PathVariable(name = "replyId") Long replyId,
                                                                          @CurrentMember Member member) {
        return ResponseEntity.ok(replyService.getReplyParent(replyId, member));
    }
}
