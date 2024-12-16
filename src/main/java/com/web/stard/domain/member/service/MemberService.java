package com.web.stard.domain.member.service;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.domain.dto.request.MemberRequestDto;
import com.web.stard.domain.member.domain.dto.response.MemberResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    MemberResponseDto.InfoDto getInfo(Member member);

    boolean checkCurrentPassword(String currentPassword, String password);

    ResponseEntity<String> editPassword(Member member, MemberRequestDto.EditPasswordDto requestDto);

    MemberResponseDto.EditNicknameResponseDto editNickname(Member member, MemberRequestDto.EditNicknameDto requestDto);

    MemberResponseDto.EditInterestResponseDto editInterest(Member member, MemberRequestDto.AdditionalInfoRequestDto requestDto);

    MemberResponseDto.ProfileImageResponseDto getProfileImage(Member member);

    MemberResponseDto.ProfileImageResponseDto updateProfileImage(MultipartFile file, Member member);

    void deleteProfileImage(Member member);

    void deleteAllRelatedEntities(Member member, boolean isForced);

    void resetPassword(String email, String password);
}
