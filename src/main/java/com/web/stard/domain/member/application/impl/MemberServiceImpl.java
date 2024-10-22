package com.web.stard.domain.member.application.impl;

import com.web.stard.domain.member.application.MemberService;
import com.web.stard.domain.member.domain.Address;
import com.web.stard.domain.member.domain.Interest;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;
import com.web.stard.domain.member.repository.InterestRepository;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.global.error.CustomException;
import com.web.stard.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final InterestRepository interestRepository;

    @Override
    public MemberResponseDto.SignupResultDto signUp(MemberRequestDto.SignupDto requestDto) {

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        Member member = requestDto.toEntity(encodedPassword);

        // 회원 정보 저장
        Member savedMember = memberRepository.save(member);

        return MemberResponseDto.SignupResultDto.from(savedMember);
    }

    /**
     * 마이페이지 - 개인정보 수정 기존 데이터 상세 조회
     *
     * @param id            사용자 고유 id
     * @return InfoDto      nickname, phone, city, district, interests
     *                      닉네임     전화번호 시    구         관심분야
     */
    @Transactional
    @Override
    public MemberResponseDto.InfoDto getInfo(Long id) {
        // 회원 정보 반환
        Member info = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Address address = info.getAddress();
        // 관심분야 반환
        List<Interest> interests = interestRepository.findAllByMember(info);

        return MemberResponseDto.InfoDto.from(info, interests);
    }


    // TODO : 비밀번호 변경 : 현재 비밀번호랑 다를 경우 변경 불가능

    // TODO : 닉네임 변경 : 사용자의 현재 닉네임 = 중복 버튼 클릭 시의 (바꿀) 닉네임 => 변경 가능 alert

    // TODO : 전화번호 변경

    // TODO : 거주지 변경

    // TODO : 관심분야 변경
}
