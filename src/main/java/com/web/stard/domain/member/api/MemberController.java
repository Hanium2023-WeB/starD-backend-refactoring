package com.web.stard.domain.member.api;

import com.web.stard.domain.post.service.PostService;
import com.web.stard.domain.reply.domain.dto.response.ReplyResponseDto;
import com.web.stard.domain.reply.service.ReplyService;
import com.web.stard.domain.starScrap.service.StarScrapService;
import com.web.stard.domain.post.domain.dto.response.PostResponseDto;
import com.web.stard.domain.member.service.MemberService;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.domain.dto.request.MemberRequestDto;
import com.web.stard.domain.member.domain.dto.response.MemberResponseDto;
import com.web.stard.domain.study.service.StudyService;
import com.web.stard.domain.teamBlog.domain.dto.response.ScheduleResponseDto;
import com.web.stard.domain.study.domain.dto.response.StudyResponseDto;
import com.web.stard.domain.teamBlog.domain.dto.response.StudyPostResponseDto;
import com.web.stard.domain.teamBlog.domain.dto.response.ToDoResponseDto;
import com.web.stard.domain.teamBlog.service.ScheduleService;
import com.web.stard.domain.teamBlog.service.StudyPostService;
import com.web.stard.domain.teamBlog.service.ToDoService;
import com.web.stard.global.domain.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final StarScrapService starScrapService;
    private final PostService postService;
    private final ToDoService toDoService;
    private final ScheduleService scheduleService;
    private final StudyPostService studyPostService;
    private final ReplyService replyService;
    private final StudyService studyService;

    @Operation(summary = "개인정보 반환")
    @GetMapping("/edit")
    public ResponseEntity<?> getInfo(@CurrentMember Member member) {
        return ResponseEntity.ok(memberService.getInfo(member));
    }

    @Operation(summary = "비밀번호 변경")
    @PostMapping("/edit/password")
    public ResponseEntity<String> editPassword(@CurrentMember Member member,
                                               @Valid @RequestBody MemberRequestDto.EditPasswordDto requestDto) {
        return memberService.editPassword(member, requestDto);
    }

    @Operation(summary = "닉네임 변경")
    @PostMapping("/edit/nickname")
    public ResponseEntity<MemberResponseDto.EditNicknameResponseDto> editNickname(@CurrentMember Member member,
                                                                                  @Valid @RequestBody MemberRequestDto.EditNicknameDto requestDto) {
        return ResponseEntity.ok(memberService.editNickname(member, requestDto));
    }

    @Operation(summary = "관심분야 변경")
    @PostMapping("/edit/interests")
    public ResponseEntity<MemberResponseDto.EditInterestResponseDto> editInterests(@CurrentMember Member member,
                                                                                   @Valid @RequestBody MemberRequestDto.AdditionalInfoRequestDto requestDto) {
        return ResponseEntity.ok(memberService.editInterest(member, requestDto));
    }

    @Operation(summary = "프로필 이미지 조회")
    @GetMapping("/profile/image")
    public ResponseEntity<MemberResponseDto.ProfileImageResponseDto> getProfileImage(@CurrentMember Member member) {
        return ResponseEntity.ok(memberService.getProfileImage(member));
    }

    @Operation(summary = "프로필 이미지 변경")
    @PutMapping(value = "/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemberResponseDto.ProfileImageResponseDto> updateProfileImage(@RequestPart(value = "file", required = false) MultipartFile file,
                                                                                        @CurrentMember Member member) {
        return ResponseEntity.ok(memberService.updateProfileImage(file, member));
    }

    @Operation(summary = "프로필 이미지 삭제")
    @DeleteMapping("/profile/image")
    public ResponseEntity<Void> deleteProfileImage(@CurrentMember Member member) {
        memberService.deleteProfileImage(member);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "사용자가 작성한 커뮤니티 게시글 조회")
    @GetMapping("/communities")
    public ResponseEntity<PostResponseDto.PostListDto> getCommPostList(@CurrentMember Member member,
                                                                       @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(postService.getCommPostListByMember(member, page));
    }

    @Operation(summary = "사용자가 작성한 댓글 조회")
    @GetMapping("/replies")
    public ResponseEntity<ReplyResponseDto.MyPageReplyListDto> getReplyList(@CurrentMember Member member,
                                                                            @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(replyService.getMemberReplyList(page, member));
    }

    @Operation(summary = "공감한 게시글 리스트 조회")
    @GetMapping("/stars")
    public ResponseEntity<PostResponseDto.PostListDto> getStarPostList(@CurrentMember Member member,
                                                                       @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(starScrapService.getMemberStarPostList(member, page));
    }

    @Operation(summary = "스크랩 스터디 리스트 조회")
    @GetMapping("/scraps")
    public ResponseEntity<StudyResponseDto.StudyRecruitListDto> getScrapStudyList(@CurrentMember Member member,
                                                                                  @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(starScrapService.getMemberScrapStudyList(member, page));
    }

    @Operation(summary = "사용자가 개설한 스터디 모집 글 리스트 조회", description = "모집 중 -> 모집 완료 -> 알 수 없음 순으로 조회됩니다.")
    @GetMapping("/studies/open")
    public ResponseEntity<StudyResponseDto.StudyRecruitListDto> getMemberOpenStudyList(@CurrentMember Member member,
                                                                                       @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(studyService.getMemberOpenStudy(member, page));
    }

    @Operation(summary = "사용자가 신청한 스터디 모집 글 리스트 조회")
    @GetMapping("/studies/apply")
    public ResponseEntity<StudyResponseDto.StudyRecruitListDto> getMemberApplyStudyList(@CurrentMember Member member,
                                                                                        @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(studyService.getMemberApplyStudy(member, page));
    }

    @Operation(summary = "사용자가 참여한(참여 중인) 스터디 모집 글 리스트 조회", description = "진행 중 -> 진행 중단 -> 진행 완료 순으로 조회됩니다.")
    @GetMapping("/studies/participate")
    public ResponseEntity<StudyResponseDto.StudyRecruitListDto> getMemberParticipateStudyList(@CurrentMember Member member,
                                                                                              @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(studyService.getMemberParticipateStudy(member, page));
    }

    @Operation(summary = "사용자 전체 ToDo 조회 - 월 단위")
    @GetMapping("/to-dos")
    public ResponseEntity<List<ToDoResponseDto.MemberToDoDto>> getToDoList(@CurrentMember Member member,
                                                                           @RequestParam(name = "year") int year,
                                                                           @RequestParam(name = "month") int month) {
        return ResponseEntity.ok(toDoService.getMemberToDoList(member, year, month));
    }

    @Operation(summary = "사용자 스터디 별 전체 ToDo 조회 - 월 단위")
    @GetMapping("/to-dos/{studyId}")
    public ResponseEntity<List<ToDoResponseDto.MemberToDoDto>> getToDoListByStudy(@CurrentMember Member member,
                                                                                  @PathVariable(name = "studyId") Long studyId,
                                                                                  @RequestParam(name = "year") int year,
                                                                                  @RequestParam(name = "month") int month) {
        return ResponseEntity.ok(toDoService.getMemberToDoListByStudy(studyId, member, year, month));
    }

    @Operation(summary = "사용자 전체 일정 조회 - 월 단위")
    @GetMapping("/schedules")
    public ResponseEntity<List<ScheduleResponseDto.ScheduleDto>> getScheduleList(@CurrentMember Member member,
                                                                                 @RequestParam(name = "year") int year,
                                                                                 @RequestParam(name = "month") int month) {
        return ResponseEntity.ok(scheduleService.getMemberScheduleList(member, year, month));
    }

    @Operation(summary = "사용자 스터디 별 일정 조회 - 월 단위")
    @GetMapping("/schedules/{studyId}")
    public ResponseEntity<List<ScheduleResponseDto.ScheduleDto>> getScheduleListByStudy(@CurrentMember Member member,
                                                                                        @PathVariable(name = "studyId") Long studyId,
                                                                                        @RequestParam(name = "year") int year,
                                                                                        @RequestParam(name = "month") int month) {
        return ResponseEntity.ok(scheduleService.getAllScheduleListByStudy(studyId, member, year, month));
    }

    @Operation(summary = "사용자가 작성한 스터디 별 팀블로그 커뮤니티 게시글 조회")
    @GetMapping("/study-posts/{studyId}")
    public ResponseEntity<StudyPostResponseDto.StudyPostListDto> getStudyPostListByStudy(@CurrentMember Member member,
                                                                                         @PathVariable(name = "studyId") Long studyId,
                                                                                         @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(studyPostService.getMemberStudyPostListByStudy(studyId, member, page));
    }

    @PutMapping("/reset-password")
    @Operation(summary = "비밀번호 재설정")
    public ResponseEntity<Boolean> resetPassword(@Valid @RequestBody MemberRequestDto.SignInDto requestDto) {
        memberService.resetPassword(requestDto.email(), requestDto.password());
        return ResponseEntity.ok().body(true);
    }
}