package com.web.stard.domain.starScrap.api;

import com.web.stard.domain.starScrap.service.StarScrapService;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.global.domain.CurrentMember;
import com.web.stard.global.exception.ApiErrorCodeExamples;
import com.web.stard.global.exception.error.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "stars-and-scraps", description = "공감 및 스크랩 관련 API")
public class StarScrapController {

    private final StarScrapService starScrapService;

    @Operation(summary = "공감 혹은 스크랩 등록", description = "공감 및 스크랩 추가할 게시글 id를 targetId에 전달해 주세요.\n\n" +
            "게시글 type을 전달해 주세요. [study / studypost / post]")
    @ApiErrorCodeExamples({
            ErrorCode.PERMISSION_DENIED, ErrorCode.DUPLICATE_STAR_SCRAP_REQUEST,
            ErrorCode.POST_NOT_FOUND, ErrorCode.PERMISSION_DENIED,
            ErrorCode.STUDY_NOT_FOUND, ErrorCode.STUDY_POST_NOT_FOUND
    })
    @PostMapping("/stars-and-scraps/{targetId}")
    public ResponseEntity<Long> addStarScrap(@CurrentMember Member member,
                                             @PathVariable(name = "targetId") Long targetId,
                                             @RequestParam(name = "tableType") String tableType) {
        return ResponseEntity.ok(starScrapService.addStarScrap(member, targetId, tableType));
    }

    @Operation(summary = "공감 혹은 스크랩 삭제", description = "공감 및 스크랩 삭제할 게시글 id를 targetId에 전달해 주세요.\n\n" +
            "게시글 type을 전달해 주세요. [study / studypost / post]")
    @ApiErrorCodeExamples({
            ErrorCode.PERMISSION_DENIED, ErrorCode.INVALID_STAR_SCRAP_REQUEST
    })
    @DeleteMapping("/stars-and-scraps/{targetId}")
    public ResponseEntity<Boolean> deleteStarScrap(@CurrentMember Member member,
                                                   @PathVariable(name = "targetId") Long targetId,
                                                   @RequestParam(name = "tableType") String tableType) {
        return ResponseEntity.ok(starScrapService.deleteStarScrap(member, targetId, tableType));
    }
}
