package com.web.stard.domain.member.application;

import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface  MemberService {
    MemberResponseDto.SignupResultDto signUp(MultipartFile file, MemberRequestDto.SignupDto requestDTO);

    boolean checkEmailDuplicate(String email);

    boolean checkNicknameDuplicate(String nickname);

    MemberResponseDto.AdditionalInfoResultDto saveAdditionalInfo(MemberRequestDto.AdditionalInfoRequestDto requestDto);

    MemberResponseDto.InfoDto getInfo(Long id);

    MemberResponseDto.EditNicknameResponseDto editNickname(Long id, MemberRequestDto.EditNicknameDto requestDTO);
}
