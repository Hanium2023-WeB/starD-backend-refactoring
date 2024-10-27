package com.web.stard.domain.member.api;

import com.web.stard.domain.member.application.MemberService;
import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입")
    @PostMapping(value = "/join", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemberResponseDto.SignupResultDto> signUp(@RequestPart(value = "file", required = false) MultipartFile file,
                                                                    @Valid @RequestPart MemberRequestDto.SignupDto requestDTO) {
        return ResponseEntity.ok(memberService.signUp(file, requestDTO));
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

    @Operation(summary = "회원가입 추가 정보 저장",
            description = "회원가입 후, 관심 분야를 저장합니다.(선택사항)")
    @PostMapping("/join/additional-info")
    public ResponseEntity<MemberResponseDto.AdditionalInfoResultDto> saveAdditionalInfo(@Valid @RequestBody MemberRequestDto.AdditionalInfoRequestDto requestDto) {
        MemberResponseDto.AdditionalInfoResultDto result = memberService.saveAdditionalInfo(requestDto);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "개인정보 반환")
    @GetMapping("/edit")
    public ResponseEntity<MemberResponseDto.InfoDto> getInfo(@RequestParam Long memberId) {
        return ResponseEntity.ok(memberService.getInfo(memberId)); // TODO 로그인 한 사용자 가져오기
    }

    @Operation(summary = "닉네임 변경")
    @PostMapping("/edit/nickname")
    public ResponseEntity<MemberResponseDto.EditNicknameResponseDto> editNickname(@RequestBody MemberRequestDto.EditNicknameDto requestDtO) {
        return ResponseEntity.ok(memberService.editNickname(requestDtO)); // TODO 로그인 한 사용자 가져오기
    }

    @Operation(summary = "관심분야 변경")
    @PostMapping("/edit/interests")
    public ResponseEntity<MemberResponseDto.EditInterestResponseDto> editInterests(@RequestBody MemberRequestDto.AdditionalInfoRequestDto requestDto) {
        return ResponseEntity.ok(memberService.editInterest(requestDto));
    }

}