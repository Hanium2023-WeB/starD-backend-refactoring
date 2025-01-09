package com.web.stard.domain.member.api;

import com.web.stard.domain.member.domain.enums.Role;
import com.web.stard.domain.member.service.AuthService;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.domain.dto.request.MemberRequestDto;
import com.web.stard.domain.member.domain.dto.response.MemberResponseDto;
import com.web.stard.global.domain.CurrentMember;
import com.web.stard.global.dto.TokenInfo;
import com.web.stard.global.exception.ApiErrorCodeExample;
import com.web.stard.global.exception.ApiErrorCodeExamples;
import com.web.stard.global.exception.error.ErrorCode;
import com.web.stard.global.utils.HeaderUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "members-auth", description = "회원 권한 관련 API")
public class AuthController {

    private final HeaderUtils headerUtils;
    private final AuthService authService;

    @Operation(summary = "회원가입")
    @ApiErrorCodeExamples({
            ErrorCode.EMAIL_CONFLICT, ErrorCode.NICKNAME_CONFLICT,
            ErrorCode.INVALID_EMAIL, ErrorCode.INVALID_NICKNAME, ErrorCode.UPLOAD_FAILED
    })
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
    @ApiErrorCodeExample(ErrorCode.MEMBER_NOT_FOUND)
    @PostMapping("/join/additional-info")
    public ResponseEntity<MemberResponseDto.AdditionalInfoResultDto> saveAdditionalInfo(@Valid @RequestBody MemberRequestDto.AdditionalInfoRequestDto requestDto) {
        MemberResponseDto.AdditionalInfoResultDto result = authService.saveAdditionalInfo(requestDto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sign-in")
    @ApiErrorCodeExample(ErrorCode.INVALID_PASSWORD)
    @Operation(summary = "로그인")
    public ResponseEntity<TokenInfo> signIn(@Valid @RequestBody MemberRequestDto.SignInDto request,
                                            HttpServletResponse response) {
        return ResponseEntity.ok(authService.signIn(request, response));
    }

    @PostMapping("/reissue")
    @ApiErrorCodeExamples({
            ErrorCode.EXPIRED_TOKEN, ErrorCode.INVALID_TOKEN, ErrorCode.EMPTY_CLAIMS
    })
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
    @ApiErrorCodeExamples({
            ErrorCode.INVALID_OR_EXPIRED_AUTH_CODE, ErrorCode.INVALID_AUTH_CODE
    })
    @Operation(summary = "인증 번호 검증")
    public ResponseEntity<Boolean> validAuthCode(@Valid @RequestBody MemberRequestDto.AuthCodeRequestDto request) throws Exception {
        authService.validAuthCode(request);
        return ResponseEntity.ok().body(true);
    }

    @PostMapping("/sign-out")
    @ApiErrorCodeExamples({
            ErrorCode.EXPIRED_TOKEN, ErrorCode.INVALID_TOKEN, ErrorCode.EMPTY_CLAIMS
    })
    @Operation(summary = "로그아웃")
    public ResponseEntity<Boolean> signOut(@CurrentMember Member member, HttpServletRequest request,
                                           HttpServletResponse response) {
        authService.signOut(member, headerUtils.resolveToken(request), response);
        return ResponseEntity.ok().body(true);
    }

    @Operation(summary = "회원 탈퇴")
    @ApiErrorCodeExamples({
            ErrorCode.MEMBER_NOT_FOUND, ErrorCode.STUDY_MEMBER_NOT_FOUND,
            ErrorCode.CANNOT_DELETE_FROM_IN_PROGRESS_STUDY, ErrorCode.DELETE_FAILED,
            ErrorCode.EXPIRED_TOKEN, ErrorCode.INVALID_TOKEN, ErrorCode.EMPTY_CLAIMS,

    })
    @DeleteMapping("/delete")
    public ResponseEntity<MemberResponseDto.DeleteDto> deleteMember(@CurrentMember Member member, HttpServletRequest request,
                                                                    HttpServletResponse response) {
        return ResponseEntity.ok(authService.deleteMember(member, headerUtils.resolveToken(request), response));
    }

    @PostMapping("/find-password")
    @ApiErrorCodeExample(ErrorCode.MEMBER_NOT_FOUND)
    @Operation(summary = "비밀번호 찾기")
    public ResponseEntity<Boolean> findPassword(@Valid @RequestBody MemberRequestDto.EmailRequestDto request) {
        authService.findPassword(request.email());
        return ResponseEntity.ok().body(true);
    }

    @GetMapping("/valid-password-reset-token")
    @ApiErrorCodeExample(ErrorCode.INVALID_TOKEN)
    @Operation(summary = "비밀번호 재설정 토큰 검증")
    public ResponseEntity<MemberResponseDto.ValidPasswordResetTokenResponseDto> validPasswordResetToken(@RequestParam(name = "token") String token) {
        MemberResponseDto.ValidPasswordResetTokenResponseDto responseDto =
                new MemberResponseDto.ValidPasswordResetTokenResponseDto(authService.validPasswordResetToken(token));
        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping
    @Operation(summary = "권한 조회", description = "관리자인 경우 ADMIN, 회원인 경우 USER가 반환됩니다.")
    public ResponseEntity<Role> getMemberRole(@CurrentMember Member member) {
        return ResponseEntity.ok(authService.getMemberRole(member));
    }

    @GetMapping("/token-expiration")
    @ApiErrorCodeExamples({
            ErrorCode.EXPIRED_TOKEN, ErrorCode.INVALID_TOKEN, ErrorCode.EMPTY_CLAIMS
    })
    @Operation(summary = "access token 남은 만료 시간 조회")
    public ResponseEntity<Long> getExpiration(@CurrentMember Member member,
                                                  HttpServletRequest request) {
        return ResponseEntity.ok(authService.getExpiration(member, request));
    }
}
