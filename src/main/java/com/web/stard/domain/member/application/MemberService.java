package com.web.stard.domain.member.application;

import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;

public interface  MemberService {
    MemberResponseDto.SignupResultDto signUp(MemberRequestDto.SignupDto requestDTO);
    MemberResponseDto.InfoDto getInfo(Long id);
}
