package com.web.stard.domain.member.api;

import com.web.stard.domain.member.application.MemberService;
import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.global.dto.TokenInfo;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/sign-in")
    @Operation(summary = "로그인")
    public ResponseEntity<TokenInfo> signIn(@Valid @RequestBody MemberRequestDto.SignInDto request) {
        return ResponseEntity.ok(memberService.signIn(request));
    }

    @PostMapping("/auth-codes")
    @Operation(summary = "인증 번호 전송")
    public ResponseEntity<?> sendAuthCode(@Valid @RequestBody MemberRequestDto.EmailRequestDto request) throws Exception {
        memberService.sendAuthCode(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth-codes/verify")
    @Operation(summary = "인증 번호 검증")
    public ResponseEntity<?> validAuthCode(@Valid @RequestBody MemberRequestDto.AuthCodeRequestDto request) throws Exception {
        memberService.validAuthCode(request);
        return ResponseEntity.ok().build();
    }

}
