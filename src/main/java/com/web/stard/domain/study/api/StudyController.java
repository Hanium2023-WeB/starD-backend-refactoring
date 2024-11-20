package com.web.stard.domain.study.api;

import com.web.stard.domain.member.service.MemberService;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.study.domain.dto.request.StudyRequestDto;
import com.web.stard.domain.study.domain.dto.response.StudyResponseDto;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.service.StudyService;
import com.web.stard.global.domain.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/studies")
@RequiredArgsConstructor
public class StudyController {

    private final MemberService memberService;
    private final StudyService studyService;

    @Operation(summary = "스터디 모집 게시글 생성")
    @PostMapping
    public ResponseEntity<Long> createStudy(@CurrentMember Member member,
                                            @Valid @RequestBody StudyRequestDto.SaveDto request) {
        Study study = request.toEntity();
        study.updateMember(member);
        Study saveStudy = studyService.createStudy(member, study);
        return ResponseEntity.ok().body(saveStudy.getId());
    }

    @Operation(summary = "스터디 모집 게시글 수정")
    @PutMapping("/{studyId}")
    public ResponseEntity<Long> updateStudy(@CurrentMember Member member, @PathVariable("studyId") Long studyId,
                                            @Valid @RequestBody StudyRequestDto.SaveDto request) {
        Study updateStudy = request.toEntity();
        updateStudy = studyService.updateStudy(member, updateStudy, studyId);
        return ResponseEntity.ok().body(updateStudy.getId());
    }

    @Operation(summary = "스터디 모집 게시글 삭제")
    @DeleteMapping("/{studyId}")
    public ResponseEntity<Void> deleteStudy(@CurrentMember Member member, @PathVariable("studyId") Long studyId) {
        studyService.deleteStudy(member, studyId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "스터디 모집 게시글 상세 조회")
    @GetMapping("/{studyId}")
    public ResponseEntity<StudyResponseDto.DetailInfo> findStudyDetailInfo(@CurrentMember Member member,
                                                                           @PathVariable("studyId") Long studyId) {
        return ResponseEntity.ok().body(studyService.findStudyDetailInfo(studyId, member));
    }


}
