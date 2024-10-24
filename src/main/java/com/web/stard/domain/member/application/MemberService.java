package com.web.stard.domain.member.application;

import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.request.MemberRequestDto.SignInDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;
import com.web.stard.global.dto.TokenInfo;

public interface  MemberService {
    MemberResponseDto.SignupResultDto signUp(MemberRequestDto.SignupDto requestDTO);

    TokenInfo signIn(SignInDto requestDTO);
}
