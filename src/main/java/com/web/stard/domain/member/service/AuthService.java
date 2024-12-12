package com.web.stard.domain.member.service;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.domain.dto.request.MemberRequestDto;
import com.web.stard.domain.member.domain.dto.response.MemberResponseDto;
import com.web.stard.global.dto.TokenInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    MemberResponseDto.SignupResultDto signUp(MultipartFile file, MemberRequestDto.SignupDto requestDto);

    boolean checkEmailDuplicate(String email);

    boolean checkNicknameDuplicate(String nickname);

    MemberResponseDto.AdditionalInfoResultDto saveAdditionalInfo(MemberRequestDto.AdditionalInfoRequestDto requestDto);

    TokenInfo signIn(MemberRequestDto.SignInDto request, HttpServletResponse response);

    void sendAuthCode(String email) throws Exception;

    void validAuthCode(MemberRequestDto.AuthCodeRequestDto request) throws Exception;

    void signOut(Member member, String token);

    TokenInfo reissue(HttpServletRequest request);
}
