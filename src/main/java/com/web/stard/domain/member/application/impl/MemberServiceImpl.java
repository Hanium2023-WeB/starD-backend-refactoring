package com.web.stard.domain.member.application.impl;

import com.web.stard.domain.member.application.MemberService;
import com.web.stard.domain.member.domain.Address;
import com.web.stard.domain.member.domain.Interest;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;
import com.web.stard.domain.member.repository.AddressRepository;
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
    private final AddressRepository addressRepository;

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

        // 관심분야 반환
        List<Interest> interests = interestRepository.findAllByMember(info);

        return MemberResponseDto.InfoDto.from(info, interests);
    }


    // TODO : 비밀번호 변경 : 현재 비밀번호랑 다를 경우 변경 불가능


    /**
     * 마이페이지 - 개인정보 수정 : 닉네임
     *
     * @param id, EditNicknameDto       사용자 고유 id, nickname 닉네임
     * @return EditNicknameResponseDto  nickname 닉네임, message 성공 메시지
     *
     */
    @Override
    public MemberResponseDto.EditNicknameResponseDto editNickname(Long id, MemberRequestDto.EditNicknameDto requestDTO) {
        // 회원 정보 반환
        Member info = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        // 닉네임 변경
        info.setNickname(requestDTO.getNickname());

        memberRepository.save(info);

        return MemberResponseDto.EditNicknameResponseDto.from(info.getNickname());
    }

    /**
     * 마이페이지 - 개인정보 수정 : 전화번호
     *
     * @param id, EditPhoneDto       사용자 고유 id, phone 전화번호
     * @return EditPhoneResponseDto  phone 전화번호, message 성공 메시지
     *
     */
    @Override
    public MemberResponseDto.EditPhoneResponseDto editPhone(Long id, MemberRequestDto.EditPhoneDto requestDTO) {
        // 회원 정보 반환
        Member info = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        // 전화번호 변경
        info.setPhone(requestDTO.getPhone());

        memberRepository.save(info);

        return MemberResponseDto.EditPhoneResponseDto.from(info.getPhone());
    }

    /**
     * 마이페이지 - 개인정보 수정 : 거주지
     *
     * @param id, EditAddressDto        사용자 고유 id, city 시, district 구
     * @return EditAddressResponseDto   city 시, district 구, message 성공 메시지
     *
     */
    @Transactional
    @Override
    public MemberResponseDto.EditAddressResponseDto editAddress(Long id, MemberRequestDto.EditAddressDto requestDTO) {
        // 회원 정보 반환
        Member info = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Address address = info.getAddress();

        // 주소가 없을 경우 생성
        if (address == null) {
            address = requestDTO.toEntity(requestDTO.getCity(), requestDTO.getDistrict());
            addressRepository.save(address); // address 객체 저장
        } else { // 있는 경우 city, district만 변경
            address.setCity(requestDTO.getCity());
            address.setDistrict(requestDTO.getDistrict());
        }

        info.setAddress(address);

        memberRepository.save(info);

        return MemberResponseDto.EditAddressResponseDto.from(info.getAddress());
    }

}
