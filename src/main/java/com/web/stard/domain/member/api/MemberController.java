package com.web.stard.domain.member.api;

import com.web.stard.domain.member.application.MemberService;
import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입")
    @PostMapping("/join")
    public ResponseEntity<MemberResponseDto.SignupResultDto> signUp(@RequestBody MemberRequestDto.SignupDto requestDTO) {
        return ResponseEntity.ok(memberService.signUp(requestDTO));
    }

    @Operation(summary = "개인정보 반환")
    @GetMapping("/edit")
    public ResponseEntity<MemberResponseDto.InfoDto> getInfo() {
        return ResponseEntity.ok(memberService.getInfo(5L)); // TODO 로그인 한 사용자 가져오기
    }

    @Operation(summary = "닉네임 수정")
    @PostMapping("/edit/nickname")
    public ResponseEntity<MemberResponseDto.EditNicknameResponseDto> editNickname(@RequestBody MemberRequestDto.EditNicknameDto requestDTO) {
        return ResponseEntity.ok(memberService.editNickname(5L, requestDTO)); // TODO 로그인 한 사용자 가져오기
    }

    @Operation(summary = "전화번호 수정")
    @PostMapping("/edit/phone")
    public ResponseEntity<MemberResponseDto.EditPhoneResponseDto> editPhone(@RequestBody MemberRequestDto.EditPhoneDto requestDTO) {
        return ResponseEntity.ok(memberService.editPhone(5L, requestDTO)); // TODO 로그인 한 사용자 가져오기
    }
}
