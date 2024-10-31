package com.web.stard.domain.member.application;

import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;
import org.springframework.http.ResponseEntity;

public interface MemberService {
    MemberResponseDto.InfoDto getInfo(Long id);

    boolean checkCurrentPassword(String currentPassword, String password);

    ResponseEntity<String> editPassword(MemberRequestDto.EditPasswordDto requestDto);

    MemberResponseDto.EditNicknameResponseDto editNickname(MemberRequestDto.EditNicknameDto requestDto);

    MemberResponseDto.EditInterestResponseDto editInterest(MemberRequestDto.AdditionalInfoRequestDto requestDto);
}
