package com.web.stard.domain.board.global.api;

import com.web.stard.domain.board.global.application.StarScrapService;
import com.web.stard.domain.board.global.domain.enums.ActType;
import com.web.stard.domain.board.global.domain.enums.TableType;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.global.domain.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StarScrapController {

    private final StarScrapService starScrapService;

    @Operation(summary = "게시글 공감 등록")
    @PostMapping("/star/{postId}")
    public ResponseEntity<Long> addPostStar(@CurrentMember Member member,
                                            @PathVariable(name = "postId") Long postId) {
        return ResponseEntity.ok(starScrapService.addStarScrap(member, postId, ActType.STAR, TableType.POST));
    }

    @Operation(summary = "스터디 스크랩 등록")
    @PostMapping("/scrap/study/{studyId}")
    public ResponseEntity<Long> addStudyScrap(@CurrentMember Member member,
                                              @PathVariable(name = "studyId") Long studyId) {
        return ResponseEntity.ok(starScrapService.addStarScrap(member, studyId, ActType.SCRAP, TableType.STUDY));
    }

    @Operation(summary = "스터디 게시글 스크랩 등록")
    @PostMapping("/scrap/studypost/{studyPostId}")
    public ResponseEntity<Long> addStudyPostScrap(@CurrentMember Member member,
                                                  @PathVariable(name = "studyPostId") Long studyPostId) {
        return ResponseEntity.ok(starScrapService.addStarScrap(member, studyPostId, ActType.SCRAP, TableType.STUDYPOST));
    }

    @Operation(summary = "게시글 공감 삭제")
    @DeleteMapping("/star/{postId}")
    public ResponseEntity<Boolean> deletePostStar(@CurrentMember Member member,
                                                  @PathVariable(name = "postId") Long postId) {
        return ResponseEntity.ok(starScrapService.deleteStarScrap(member, postId, ActType.STAR, TableType.POST));
    }

    @Operation(summary = "스터디 스크랩 삭제")
    @DeleteMapping("/scrap/study/{studyId}")
    public ResponseEntity<Boolean> deleteStudyScrap(@CurrentMember Member member,
                                                    @PathVariable(name = "studyId") Long studyId) {
        return ResponseEntity.ok(starScrapService.deleteStarScrap(member, studyId, ActType.SCRAP, TableType.STUDY));
    }

    @Operation(summary = "스터디 게시글 스크랩 삭제")
    @DeleteMapping("/scrap/studypost/{studyPostId}")
    public ResponseEntity<Boolean> deleteStudyPostScrap(@CurrentMember Member member,
                                                        @PathVariable(name = "studyPostId") Long studyPostId) {
        return ResponseEntity.ok(starScrapService.deleteStarScrap(member, studyPostId, ActType.SCRAP, TableType.STUDYPOST));
    }
}
