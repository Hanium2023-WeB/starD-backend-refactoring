package com.web.stard.domain.study.api;

import com.web.stard.domain.member.service.MemberService;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.study.domain.dto.request.StudyRequestDto;
import com.web.stard.domain.study.domain.dto.response.StudyResponseDto;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.service.StudyService;
import com.web.stard.domain.study.service.StudyTagService;
import com.web.stard.global.domain.CurrentMember;
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
    private final StudyTagService studyTagService;

    @PostMapping
    public ResponseEntity<Long> createStudy(@CurrentMember Member member, @Valid @RequestBody StudyRequestDto.CreateDto request) {
        Study study = request.toEntity();
        study.updateMember(member);
        Study saveStudy = studyService.createStudy(member, study);
        studyTagService.createStudyTags(saveStudy);
        return ResponseEntity.ok().body(saveStudy.getId());
    }

    @GetMapping("/{studyId}")
    public ResponseEntity<StudyResponseDto.DetailInfo> findStudyDetailInfo(@CurrentMember Member member,
                                                                @PathVariable("studyId") Long studyId) {
        return ResponseEntity.ok().body(studyService.findStudyDetailInfo(studyId, member));
    }


}
