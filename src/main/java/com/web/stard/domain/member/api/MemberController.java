package com.web.stard.domain.member.api;

import com.web.stard.domain.board.global.service.PostService;
import com.web.stard.domain.board.global.service.StarScrapService;
import com.web.stard.domain.board.global.dto.response.PostResponseDto;
import com.web.stard.domain.member.service.MemberService;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;
import com.web.stard.domain.study.domain.dto.response.StudyResponseDto;
import com.web.stard.domain.study.domain.dto.response.ToDoResponseDto;
import com.web.stard.domain.study.service.ToDoService;
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

    @Operation(summary = "공감한 게시글 리스트 조회")
    @GetMapping("/stars")
    public ResponseEntity<PostResponseDto.PostListDto> getStarPostList(@CurrentMember Member member,
                                                                       @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(starScrapService.getMemberStarPostList(member, page));
    }

    @Operation(summary = "사용자가 작성한 커뮤니티 게시글 조회")
    @GetMapping("/communities")
    public ResponseEntity<PostResponseDto.PostListDto> getCommPostList(@CurrentMember Member member,
                                                                       @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(postService.getCommPostListByMember(member, page));
    }

    @Operation(summary = "스크랩 스터디 리스트 조회")
    @GetMapping("/scraps")
    public ResponseEntity<StudyResponseDto.StudyRecruitListDto> getScrapStudyList(@CurrentMember Member member,
                                                                                  @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(starScrapService.getMemberScrapStudyList(member, page));
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
}