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

    @Operation(summary = "이메일 중복 확인", description = "입력한 이메일을 다른 사용자가 사용 중인지 확인합니다.\n\n" +
            "사용 가능하면 true, 이미 사용 중이면 false 값을 반환합니다.")
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkDuplicateID(@RequestParam(name = "email") String email) {
        return ResponseEntity.ok(memberService.checkEmailDuplicate(email));
    }

    @Operation(summary = "닉네임 중복 확인", description = "입력한 닉네임을 다른 사용자가 사용 중인지 확인합니다.\n\n" +
            "사용 가능하면 true, 이미 사용 중이면 false 값을 반환합니다.")
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkDuplicateNickname(@RequestParam(name = "nickname") String nickname) {
        return ResponseEntity.ok(memberService.checkNicknameDuplicate(nickname));
    }

}
