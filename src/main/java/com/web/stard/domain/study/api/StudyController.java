package com.web.stard.domain.study.api;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.domain.enums.InterestField;
import com.web.stard.domain.study.domain.dto.request.StudyRequestDto;
import com.web.stard.domain.study.domain.dto.response.StudyResponseDto;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.Tag;
import com.web.stard.domain.study.domain.enums.ActivityType;
import com.web.stard.domain.study.domain.enums.RecruitmentType;
import com.web.stard.domain.study.service.StudyService;
import com.web.stard.domain.teamBlog.domain.dto.response.StudyPostResponseDto;
import com.web.stard.domain.teamBlog.service.StudyPostService;
import com.web.stard.global.domain.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final StudyPostService studyPostService;


    @Operation(summary = "스터디 모집 게시글 생성")
    @PostMapping
    public ResponseEntity<Long> createStudy(@CurrentMember Member member,
                                            @Valid @RequestBody StudyRequestDto.Save request) {
        Study study = request.toEntity();
        study.updateMember(member);
        Study saveStudy = studyService.createStudy(member, study);
        return ResponseEntity.ok().body(saveStudy.getId());
    }

    @Operation(summary = "주간 인기 태그 Top 5 조회")
    @GetMapping
    public ResponseEntity<StudyResponseDto.TagInfosDto> getHotTagTop5() {
        List<Tag> tags = studyService.getHotTagTop5();
        StudyResponseDto.TagInfosDto response = StudyResponseDto.TagInfosDto.toDto(
                tags.stream().map(StudyResponseDto.TagInfoDto::toDto).toList());
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "스터디 모집 게시글 수정")
    @PutMapping("/{studyId}")
    public ResponseEntity<Long> updateStudy(@CurrentMember Member member, @PathVariable("studyId") Long studyId,
                                            @Valid @RequestBody StudyRequestDto.Save request) {
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
    public ResponseEntity<StudyResponseDto.DetailInfo> getStudyDetailInfo(@CurrentMember Member member,
                                                                          @PathVariable("studyId") Long studyId) {
        return ResponseEntity.ok().body(studyService.findStudyDetailInfo(studyId, member));
    }

    @Operation(summary = "스터디 참여 신청")
    @PostMapping("/{studyId}/applications")
    public ResponseEntity<?> registerApplication(@CurrentMember Member member, @PathVariable("studyId") Long studyId,
                                                 @Valid @RequestBody StudyRequestDto.ApplyStudy request) {
        studyService.registerApplication(member, studyId, request.toEntity());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "스터디 신청자 선택")
    @PostMapping("/{studyId}/applications/{applicationId}/assignment")
    public ResponseEntity<?> selectApplicant(@CurrentMember Member member, @PathVariable("studyId") Long studyId,
                                             @PathVariable(name = "applicationId") Long applicationId) {
        studyService.selectApplicant(member, studyId, applicationId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "스터디 신청자 목록 조회")
    @GetMapping("/{studyId}/applications")
    public ResponseEntity<List<StudyResponseDto.StudyApplicantInfo>> getApplicants(@CurrentMember Member member,
                                                                                   @PathVariable("studyId") Long studyId) {
        return ResponseEntity.ok().body(studyService.getApplicants(member, studyId).stream()
                .map(StudyResponseDto.StudyApplicantInfo::toDto).toList());
    }

    @Operation(summary = "스터디 검색")
    @GetMapping("/search")
    public ResponseEntity<StudyResponseDto.StudyInfoListDto> searchStudies(
            @RequestParam(defaultValue = "1", name = "page") int page,
            @RequestParam(defaultValue = "9", name = "size") int size,
            @RequestParam(required = false, name = "keyword") String keyword, @RequestParam(required = false, name = "tags") String tags,
            @RequestParam(required = false, name = "recruitmentType") RecruitmentType recruitmentType,
            @RequestParam(required = false, name = "activityType") ActivityType activityType,
            @RequestParam(required = false, name = "city") String city, @RequestParam(required = false, name = "district") String district,
            @RequestParam(required = false, name = "field") InterestField field,
            @CurrentMember Member member) {
        Pageable pageable = PageRequest.of(page - 1, size);
        StudyRequestDto.StudySearchFilter filter = StudyRequestDto.StudySearchFilter.of(page, size, keyword, tags,
                recruitmentType, activityType, city, district, field);
        return ResponseEntity.ok().body(StudyResponseDto.StudyInfoListDto.of(studyService.searchStudies(member, filter, pageable)));
    }

    @Operation(summary = "스터디 팀블로그 개설")
    @ApiResponse(
            responseCode = "400",
            description = "BAD_REQUEST",
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "스터디 참여자 최소 3명 이상",
                                    value = """
                                            {
                                              "status": 400,
                                              "message": "스터디 참여자는 최소 3명 이상이어야 합니다.",
                                              "errorCode": "STUDY_MINIMUM_MEMBERS_REQUIRED"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "스터디 모집 인원 초과",
                                    value = """
                                            {
                                              "status": 400,
                                              "message": "스터디 모집 인원을 초과했습니다.",
                                              "errorCode": "STUDY_MEMBER_LIMIT_EXCEEDED"
                                            }
                                            """
                            )
                    }
            )
    )
    @PostMapping("/{studyId}/open")
    public ResponseEntity<Long> openStudy(@CurrentMember Member member, @PathVariable("studyId") Long studyId) {
        return ResponseEntity.ok().body(studyService.openStudy(member, studyId));
    }

    @Operation(summary = "팀블로그 스터디원 목록 조회")
    @GetMapping("/{studyId}/members")
    public ResponseEntity<List<StudyResponseDto.StudyMemberInfo>> getStudyMembers(@CurrentMember Member member, @PathVariable("studyId") Long studyId) {
        return ResponseEntity.ok().body(studyService.getStudyMembers(member, studyId));
    }

    @Operation(summary = "팀블로그 id로 스터디 게시글 id 조회", description = "신고 내역에서 대상 글을 조회할 때 사용합니다.")
    @GetMapping("/{studyPostId}/parent")
    public ResponseEntity<StudyPostResponseDto.StudyPostParentDto> getStudyPostParent(@PathVariable(name = "studyPostId") Long studyPostId,
                                                                                      @CurrentMember Member member) {
        return ResponseEntity.ok(studyPostService.getStudyPostParent(studyPostId, member));
    }
}
