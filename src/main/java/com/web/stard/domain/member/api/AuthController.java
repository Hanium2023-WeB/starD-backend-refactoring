package com.web.stard.domain.member.api;

import com.web.stard.domain.member.application.AuthService;
import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;
import com.web.stard.global.dto.TokenInfo;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/members/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @Operation(summary = "회원가입")
    @PostMapping(value = "/join", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemberResponseDto.SignupResultDto> signUp(@RequestPart(value = "file", required = false) MultipartFile file,
                                                                    @Valid @RequestPart(name = "requestDto") MemberRequestDto.SignupDto requestDto) {
        return ResponseEntity.ok(authService.signUp(file, requestDto));
    }

    @Operation(summary = "이메일 중복 확인", description = "입력한 이메일을 다른 사용자가 사용 중인지 확인합니다.\n\n" +
            "사용 가능하면 true, 이미 사용 중이면 false 값을 반환합니다.")
    @PostMapping("/check-email")
    public ResponseEntity<Boolean> checkDuplicateID(@Valid @RequestBody MemberRequestDto.EmailRequestDto requestDto) {
        return ResponseEntity.ok(authService.checkEmailDuplicate(requestDto.email()));
    }

    @Operation(summary = "닉네임 중복 확인", description = "입력한 닉네임을 다른 사용자가 사용 중인지 확인합니다.\n\n" +
            "사용 가능하면 true, 이미 사용 중이면 false 값을 반환합니다.")
    @PostMapping("/check-nickname")
    public ResponseEntity<Boolean> checkDuplicateNickname(@Valid @RequestBody MemberRequestDto.NicknameDto requestDto) {
        return ResponseEntity.ok(authService.checkNicknameDuplicate(requestDto.getNickname()));
    }

    @Operation(summary = "회원가입 추가 정보 저장",
            description = "회원가입 후, 관심 분야를 저장합니다.(선택사항)")
    @PostMapping("/join/additional-info")
    public ResponseEntity<MemberResponseDto.AdditionalInfoResultDto> saveAdditionalInfo(@Valid @RequestBody MemberRequestDto.AdditionalInfoRequestDto requestDto) {
        MemberResponseDto.AdditionalInfoResultDto result = authService.saveAdditionalInfo(requestDto);
        return ResponseEntity.ok(result);
    }
    
    // 로그인
    @PostMapping("/sign-in")
    @Operation(summary = "로그인")
    public ResponseEntity<TokenInfo> signIn(@Valid @RequestBody MemberRequestDto.SignInDto request) {
        return ResponseEntity.ok(authService.signIn(request));
    }

    @PostMapping("/auth-codes")
    @Operation(summary = "인증 번호 전송")
    public ResponseEntity<?> sendAuthCode(@Valid @RequestBody MemberRequestDto.EmailRequestDto request) throws Exception {
        authService.sendAuthCode(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth-codes/verify")
    @Operation(summary = "인증 번호 검증")
    public ResponseEntity<?> validAuthCode(@Valid @RequestBody MemberRequestDto.AuthCodeRequestDto request) throws Exception {
        authService.validAuthCode(request);
        return ResponseEntity.ok().build();
    }

}
