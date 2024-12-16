package com.web.stard.domain.member.api;

import com.web.stard.domain.member.service.AuthService;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.domain.dto.request.MemberRequestDto;
import com.web.stard.domain.member.domain.dto.response.MemberResponseDto;
import com.web.stard.global.domain.CurrentMember;
import com.web.stard.global.dto.TokenInfo;
import com.web.stard.global.utils.HeaderUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    private final HeaderUtils headerUtils;
    private final AuthService authService;

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
    public ResponseEntity<Boolean> checkDuplicateNickname(@Valid @RequestBody MemberRequestDto.EditNicknameDto requestDto) {
        return ResponseEntity.ok(authService.checkNicknameDuplicate(requestDto.getNickname()));
    }

    @Operation(summary = "회원가입 추가 정보 저장",
            description = "회원가입 후, 관심 분야를 저장합니다.(선택사항)")
    @PostMapping("/join/additional-info")
    public ResponseEntity<MemberResponseDto.AdditionalInfoResultDto> saveAdditionalInfo(@Valid @RequestBody MemberRequestDto.AdditionalInfoRequestDto requestDto) {
        MemberResponseDto.AdditionalInfoResultDto result = authService.saveAdditionalInfo(requestDto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sign-in")
    @Operation(summary = "로그인")
    public ResponseEntity<TokenInfo> signIn(@Valid @RequestBody MemberRequestDto.SignInDto request,
                                            HttpServletResponse response) {
        return ResponseEntity.ok(authService.signIn(request, response));
    }

    @PostMapping("/reissue")
    @Operation(summary = "JWT 토큰 재발급")
    public ResponseEntity<TokenInfo> reissue(HttpServletResponse response, HttpServletRequest request) {
        return ResponseEntity.ok(authService.reissue(response, request));
    }

    @PostMapping("/auth-codes")
    @Operation(summary = "인증 번호 전송")
    public ResponseEntity<?> sendAuthCode(@Valid @RequestBody MemberRequestDto.EmailRequestDto request) throws Exception {
        authService.sendAuthCode(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth-codes/verify")
    @Operation(summary = "인증 번호 검증")
    public ResponseEntity<Boolean> validAuthCode(@Valid @RequestBody MemberRequestDto.AuthCodeRequestDto request) throws Exception {
        authService.validAuthCode(request);
        return ResponseEntity.ok().body(true);
    }

    @PostMapping("/sign-out")
    @Operation(summary = "로그아웃")
    public ResponseEntity<Boolean> signOut(@CurrentMember Member member, HttpServletRequest request,
                                           HttpServletResponse response) {
        authService.signOut(member, headerUtils.resolveToken(request), response);
        return ResponseEntity.ok().body(true);
    }

    @PostMapping("/find-password")
    @Operation(summary = "비밀번호 찾기")
    public ResponseEntity<Boolean> findPassword(@Valid @RequestBody MemberRequestDto.EmailRequestDto request) {
        authService.findPassword(request.email());
        return ResponseEntity.ok().body(true);
    }

    @GetMapping("/valid-password-reset-token")
    @Operation(summary = "비밀번호 재설정 토큰 검증")
    public ResponseEntity<Boolean> validPasswordResetToken(@RequestParam(name = "token") String token) {
        authService.validPasswordResetToken(token);
        return ResponseEntity.ok().body(true);
    }

}
