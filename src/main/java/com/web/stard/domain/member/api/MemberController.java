package com.web.stard.domain.member.api;

import com.web.stard.domain.member.application.MemberService;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;
import com.web.stard.global.domain.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "개인정보 반환")
    @GetMapping("/edit")
    public ResponseEntity<?> getInfo(@CurrentMember Member member) {
        MemberResponseDto.InfoDto response = MemberResponseDto.InfoDto.of(member);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 변경")
    @PostMapping("/edit/password")
    public ResponseEntity<String> editPassword(@Valid @RequestBody MemberRequestDto.EditPasswordDto requestDto) {
        return memberService.editPassword(requestDto);
    }

    @Operation(summary = "닉네임 변경")
    @PostMapping("/edit/nickname")
    public ResponseEntity<MemberResponseDto.EditNicknameResponseDto> editNickname(@Valid @RequestBody MemberRequestDto.EditNicknameDto requestDto) {
        return ResponseEntity.ok(memberService.editNickname(requestDto)); // TODO 로그인 한 사용자 가져오기
    }

    @Operation(summary = "관심분야 변경")
    @PostMapping("/edit/interests")
    public ResponseEntity<MemberResponseDto.EditInterestResponseDto> editInterests(@Valid @RequestBody MemberRequestDto.AdditionalInfoRequestDto requestDto) {
        return ResponseEntity.ok(memberService.editInterest(requestDto));
    }

}