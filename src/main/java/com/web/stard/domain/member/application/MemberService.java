package com.web.stard.domain.member.application;

import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.request.MemberRequestDto.SignInDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;
import com.web.stard.global.dto.TokenInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    MemberResponseDto.SignupResultDto signUp(MultipartFile file, MemberRequestDto.SignupDto requestDTO);

    boolean checkEmailDuplicate(String email);

    boolean checkNicknameDuplicate(String nickname);

    MemberResponseDto.AdditionalInfoResultDto saveAdditionalInfo(MemberRequestDto.AdditionalInfoRequestDto requestDto);

    MemberResponseDto.InfoDto getInfo(Long id);

    boolean checkCurrentPassword(String currentPassword, String password);

    ResponseEntity<String> editPassword(MemberRequestDto.EditPasswordDto requestDto);

    MemberResponseDto.EditNicknameResponseDto editNickname(MemberRequestDto.EditNicknameDto requestDto);

    MemberResponseDto.EditInterestResponseDto editInterest(MemberRequestDto.AdditionalInfoRequestDto requestDto);

    TokenInfo signIn(SignInDto request);
}
