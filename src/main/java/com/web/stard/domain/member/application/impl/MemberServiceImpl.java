package com.web.stard.domain.member.application.impl;

import com.web.stard.domain.member.application.MemberService;
import com.web.stard.domain.member.domain.Address;
import com.web.stard.domain.member.domain.Interest;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.domain.Profile;
import com.web.stard.domain.member.domain.enums.InterestField;
import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;
import com.web.stard.domain.member.repository.InterestRepository;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.global.config.aws.S3Manager;
import com.web.stard.global.error.CustomException;
import com.web.stard.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Manager s3Manager;

    @Override
    public MemberResponseDto.SignupResultDto signUp(MultipartFile file, MemberRequestDto.SignupDto requestDto) {

        String fileUrl = null;

        // UUID 생성 및 키 이름 생성
        if (file != null && !file.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String keyName = s3Manager.generateProfileKeyName(uuid);

            // S3에 파일 업로드
            fileUrl = s3Manager.uploadFile(keyName, file);
        }

        // 프로필 생성
        Profile profile = Profile.builder()
                .credibility(5.0)
                .imgUrl(fileUrl)
                .build();

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 회원 정보 저장
        Member member = requestDto.toEntity(encodedPassword, profile);
        Member savedMember = memberRepository.save(member);

        return MemberResponseDto.SignupResultDto.from(savedMember);
    }

    @Override
    public boolean checkEmailDuplicate(String email) {
        return !memberRepository.existsByEmail(email);
    }

    @Override
    public boolean checkNicknameDuplicate(String nickname) {
        return !memberRepository.existsByNickname(nickname);
    }

    @Override
    public MemberResponseDto.AdditionalInfoResultDto saveAdditionalInfo(MemberRequestDto.AdditionalInfoRequestDto requestDto) {
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 거주지 저장
        if (requestDto.getCity() != null && requestDto.getDistrict() != null) {
            Address address = Address.builder()
                    .city(requestDto.getCity())
                    .district(requestDto.getDistrict())
                    .build();
            member.setAddress(address);
        }
        // 관심 분야 저장
        if (requestDto.getInterests() != null && !requestDto.getInterests().isEmpty()) {
            requestDto.getInterests().forEach(interest -> {
                Interest interestEntity = Interest.builder()
                        .interestField(InterestField.valueOf(interest))
                        .member(member)
                        .build();
                interestRepository.save(interestEntity);
            });
        }

        // 회원 정보 저장
        memberRepository.save(member);

        return MemberResponseDto.AdditionalInfoResultDto.of(member);
    }

}
