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
import com.web.stard.global.exception.ApiErrorCodeExample;
import com.web.stard.global.exception.ApiErrorCodeExamples;
import com.web.stard.global.exception.error.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
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
@io.swagger.v3.oas.annotations.tags.Tag(name = "studies", description = "스터디 관련 API")
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
    @GetMapping("/hot-tag")
    public ResponseEntity<StudyResponseDto.TagInfosDto> getHotTagTop5() {
        List<Tag> tags = studyService.getHotTagTop5();
        StudyResponseDto.TagInfosDto response = StudyResponseDto.TagInfosDto.toDto(
                tags.stream().map(StudyResponseDto.TagInfoDto::toDto).toList());
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "스터디 모집 게시글 수정")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND, ErrorCode.STUDY_FORBIDDEN, ErrorCode.STUDY_NOT_EDITABLE
    })
    @PutMapping("/{studyId}")
    public ResponseEntity<Long> updateStudy(@CurrentMember Member member, @PathVariable("studyId") Long studyId,
                                            @Valid @RequestBody StudyRequestDto.Save request) {
        Study updateStudy = request.toEntity();
        updateStudy = studyService.updateStudy(member, updateStudy, studyId);
        return ResponseEntity.ok().body(updateStudy.getId());
    }

    @Operation(summary = "스터디 모집 게시글 삭제")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND, ErrorCode.STUDY_FORBIDDEN
    })
    @DeleteMapping("/{studyId}")
    public ResponseEntity<Void> deleteStudy(@CurrentMember Member member, @PathVariable("studyId") Long studyId) {
        studyService.deleteStudy(member, studyId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "스터디 모집 게시글 상세 조회")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND, ErrorCode.MEMBER_NOT_FOUND
    })
    @GetMapping("/{studyId}")
    public ResponseEntity<StudyResponseDto.DetailInfo> getStudyDetailInfo(@CurrentMember Member member,
                                                                          @PathVariable("studyId") Long studyId) {
        return ResponseEntity.ok().body(studyService.findStudyDetailInfo(studyId, member));
    }

    @Operation(summary = "스터디 참여 신청")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND, ErrorCode.MEMBER_NOT_FOUND, ErrorCode.STUDY_DUPLICATE_APPLICATION,
            ErrorCode.STUDY_APPLICATION_LIMIT_EXCEEDED
    })
    @PostMapping("/{studyId}/applications")
    public ResponseEntity<?> registerApplication(@CurrentMember Member member, @PathVariable("studyId") Long studyId,
                                                 @Valid @RequestBody StudyRequestDto.ApplyStudy request) {
        studyService.registerApplication(member, studyId, request.toEntity());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "스터디 지원 동기 정보 조회")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND, ErrorCode.MEMBER_NOT_FOUND
    })
    @GetMapping("/{studyId}/application")
    public ResponseEntity<StudyResponseDto.StudyApplicantInfo> getApplicationInfo(@CurrentMember Member member, @PathVariable("studyId") Long studyId) {
        return ResponseEntity.ok().body(studyService.getApplicationInfo(member, studyId));
    }

    @Operation(summary = "스터디 신청자 선택")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND, ErrorCode.MEMBER_NOT_FOUND, ErrorCode.STUDY_APPLICATION_NOT_FOUND
    })
    @PostMapping("/{studyId}/applications/{applicationId}/assignment")
    public ResponseEntity<?> selectApplicant(@CurrentMember Member member, @PathVariable("studyId") Long studyId,
                                             @PathVariable(name = "applicationId") Long applicationId) {
        studyService.selectApplicant(member, studyId, applicationId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "스터디 신청자 목록 조회")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND, ErrorCode.MEMBER_NOT_FOUND, ErrorCode.STUDY_FORBIDDEN
    })
    @GetMapping("/{studyId}/applications")
    public ResponseEntity<List<StudyResponseDto.StudyApplicantInfo>> getApplicants(@CurrentMember Member member,
                                                                                   @PathVariable("studyId") Long studyId) {
        return ResponseEntity.ok().body(studyService.getApplicants(member, studyId));
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
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND, ErrorCode.STUDY_FORBIDDEN, ErrorCode.STUDY_DUPLICATE_OPEN,
            ErrorCode.STUDY_MINIMUM_MEMBERS_REQUIRED, ErrorCode.STUDY_MEMBER_LIMIT_EXCEEDED
    })
    @PostMapping("/{studyId}/open")
    public ResponseEntity<Long> openStudy(@CurrentMember Member member, @PathVariable("studyId") Long studyId) {
        return ResponseEntity.ok().body(studyService.openStudy(member, studyId));
    }

    @Operation(summary = "팀블로그 스터디원 목록 조회")
    @ApiErrorCodeExample(ErrorCode.STUDY_NOT_FOUND)
    @GetMapping("/{studyId}/members")
    public ResponseEntity<List<StudyResponseDto.StudyMemberInfo>> getStudyMembers(@CurrentMember Member member, @PathVariable("studyId") Long studyId) {
        return ResponseEntity.ok().body(studyService.getStudyMembers(member, studyId));
    }

    @Operation(summary = "팀블로그 id로 스터디 게시글 id 조회", description = "신고 내역에서 대상 글을 조회할 때 사용합니다.")
    @ApiErrorCodeExample(ErrorCode.STUDY_POST_NOT_FOUND)
    @GetMapping("/{studyPostId}/parent")
    public ResponseEntity<StudyPostResponseDto.StudyPostParentDto> getStudyPostParent(@PathVariable(name = "studyPostId") Long studyPostId,
                                                                                      @CurrentMember Member member) {
        return ResponseEntity.ok(studyPostService.getStudyPostParent(studyPostId, member));
    }

    @Operation(summary = "인기 있는 스터디 분야 Top 5")
    @GetMapping("/hot-field")
    public ResponseEntity<List<StudyResponseDto.StudyFieldInfoDto>> getTop5HotStudyFields() {
        return ResponseEntity.ok(studyService.getTop5HotStudyFields());
    }

    @Operation(summary = "진행 중인 스터디 삭제 동의")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND, ErrorCode.STUDY_MEMBER_NOT_FOUND
    })
    @PutMapping("/{studyId}/consent")
    public ResponseEntity<Void> agreeToStudyDeletion(@CurrentMember Member member, @PathVariable(name = "studyId") Long studyId) {
        studyService.agreeToStudyDeletion(member, studyId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "진행 중인 스터디 삭제 동의 상태 조회")
    @GetMapping("/{studyId}/consents")
    public ResponseEntity<List<StudyResponseDto.StudyMemberDeletionInfo>> getStudyDeletionConsentStatus(@CurrentMember Member member, @PathVariable(name = "studyId") Long studyId) {
        return ResponseEntity.ok().body(studyService.getStudyDeletionConsentStatus(member, studyId));
    }

    @Operation(summary = "스터디 중단")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND, ErrorCode.STUDY_NOT_MEMBER, ErrorCode.STUDY_NOT_CANCELED
    })
    @DeleteMapping("/{studyId}/cancel")
    public ResponseEntity<Void> canceledStudy(@CurrentMember Member member, @PathVariable(name = "studyId") Long studyId) {
        studyService.canceledStudy(member, studyId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "진행 중인 스터디 팀 블로그 목록 조회")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND, ErrorCode.STUDY_NOT_MEMBER, ErrorCode.STUDY_NOT_CANCELED
    })
    @GetMapping("/teamBlogs")
    public ResponseEntity<List<StudyResponseDto.DetailInfo>> getTeamBlogs(@CurrentMember Member member) {
        return ResponseEntity.ok().body(studyService.getTeamBlogs(member));
    }
}
