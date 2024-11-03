package com.web.stard.domain.member.application;

import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;
import com.web.stard.global.dto.TokenInfo;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    MemberResponseDto.SignupResultDto signUp(MultipartFile file, MemberRequestDto.SignupDto requestDto);

    boolean checkEmailDuplicate(String email);

    boolean checkNicknameDuplicate(String nickname);

    MemberResponseDto.AdditionalInfoResultDto saveAdditionalInfo(MemberRequestDto.AdditionalInfoRequestDto requestDto);

    TokenInfo signIn(MemberRequestDto.SignInDto request);

    void sendAuthCode(String email) throws Exception;

    void validAuthCode(MemberRequestDto.AuthCodeRequestDto request) throws Exception;

    void signOut(Member member, String token);
}
