package com.web.stard.domain.member.application;

import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;

public interface  MemberService {
    MemberResponseDto.SignupResultDto signUp(MemberRequestDto.SignupDto requestDTO);

    boolean checkEmailDuplicate(String email);

    boolean checkNicknameDuplicate(String nickname);

    MemberResponseDto.AdditionalInfoResultDto saveAdditionalInfo(MemberRequestDto.AdditionalInfoRequestDto requestDto);
}
