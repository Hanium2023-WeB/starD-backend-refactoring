package com.web.stard.domain.member.application;

import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;
import org.springframework.http.ResponseEntity;

public interface MemberService {
    MemberResponseDto.InfoDto getInfo(Member member);

    boolean checkCurrentPassword(String currentPassword, String password);

    ResponseEntity<String> editPassword(Member member, MemberRequestDto.EditPasswordDto requestDto);

    MemberResponseDto.EditNicknameResponseDto editNickname(Member member, MemberRequestDto.EditNicknameDto requestDto);

    MemberResponseDto.EditInterestResponseDto editInterest(Member member, MemberRequestDto.AdditionalInfoRequestDto requestDto);
}
