package com.web.stard.domain.member.application.impl;

import com.web.stard.domain.member.application.MemberService;
import com.web.stard.domain.member.domain.Interest;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.domain.enums.InterestField;
import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;
import com.web.stard.domain.member.repository.InterestRepository;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 현재 비밀번호 확인
     *
     * @param currentPassword 사용자 현재 비밀번호, password 사용자가 입력한 비밀번호
     * @return boolean 비밀번호가 맞으면 true, 틀리면 false
     */
    @Override
    public boolean checkCurrentPassword(String currentPassword, String password) {
        return passwordEncoder.matches(password, currentPassword); // 입력한 비밀번호와 사용자 비밀번호 같음
    }

    /**
     * 마이페이지 - 개인정보 수정 기존 데이터 상세 조회
     *
     * @param memberId   사용자 고유 id
     * @return InfoDto   nickname, interests
     *                   닉네임     관심분야
     */
    @Transactional(readOnly = true)
    @Override
    public MemberResponseDto.InfoDto getInfo(Long memberId) {
        // 회원 정보 반환
        Member info = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return MemberResponseDto.InfoDto.of(info);
    }

    /**
     * 마이페이지 - 개인정보 수정 : 비밀번호
     *
     * @param requestDto 사용자 고유 id, password 비밀번호
     * @return 없음
     */
    @Transactional
    @Override
    public ResponseEntity<String> editPassword(MemberRequestDto.EditPasswordDto requestDto) {
        // 회원 정보 반환
        Member info = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 현재 비밀번호 확인
        if (!checkCurrentPassword(info.getPassword(), requestDto.getOriginPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        info.updatePassword(encodedPassword);

        return ResponseEntity.status(HttpStatus.OK).body("비밀번호를 변경하였습니다.");
    }

    /**
     * 마이페이지 - 개인정보 수정 : 닉네임
     *
     * @param requestDto                사용자 고유 id, nickname 닉네임
     * @return EditNicknameResponseDto  nickname 닉네임, message 성공 메시지
     */
    @Transactional
    @Override
    public MemberResponseDto.EditNicknameResponseDto editNickname(MemberRequestDto.EditNicknameDto requestDto) {
        // 회원 정보 반환
        Member info = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 닉네임 중복 확인
        if (memberRepository.existsByNickname(requestDto.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_CONFLICT);
        }
        
        // 닉네임 변경
        info.updateNickname(requestDto.getNickname());

        return MemberResponseDto.EditNicknameResponseDto.of(info.getNickname());
    }

    /**
     * 마이페이지 - 개인정보 수정 : 관심분야
     * 기존 관심분야 삭제 후 새로 삽입
     * @param requestDto : EditInterestDto  사용자 고유 id, interestField 관심분야
     * @return EditInterestResponseDto      interests 관심분야, message 성공 메시지
     *
     */
    @Transactional
    @Override
    public MemberResponseDto.EditInterestResponseDto editInterest(MemberRequestDto.AdditionalInfoRequestDto requestDto) {
        // 회원 정보 반환
        Member info = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 기존 관심분야와 비교 후 삭제 및 추가
        List<Interest> interests = new ArrayList<>(info.getInterests());
        Iterator<Interest> iterator = interests.iterator();

        while (iterator.hasNext()) {
            Interest interest = iterator.next();

            if (!requestDto.getInterests().stream()
                    .anyMatch(field -> field.equals(interest.getInterestField().getDescription()))) {
                // 변경할 관심분야에 없는 기존 관심분야 삭제
                interest.deleteInterest(); // 관계 삭제
                info.getInterests().remove(interest); // 관계 삭제
                interestRepository.delete(interest);
                iterator.remove(); // 요소에서도 삭제
            }
        }

        requestDto.getInterests().forEach(interestField -> {
            if (!interests.stream().anyMatch(interest -> interest.getInterestField().getDescription().equals(interestField))) { // 새로운 관심분야 추가
                // 기존에 없던 관심분야 추가
                Interest interestEntity = Interest.builder()
                        .interestField(InterestField.find(interestField))
                        .member(info)
                        .build();
                interestRepository.save(interestEntity); // 관심분야 추가
                interests.add(interestEntity); // 요소에 추가
            }
        });

        return MemberResponseDto.EditInterestResponseDto.of(interests);
    }

}
