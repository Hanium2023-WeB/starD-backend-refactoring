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
    @GetMapping("/update")
    public ResponseEntity<MemberResponseDto.InfoDto> getInfo() {
        return ResponseEntity.ok(memberService.getInfo(5L)); // TODO 로그인 한 사용자 가져오기
    }
}
