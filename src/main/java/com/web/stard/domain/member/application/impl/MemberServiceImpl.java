package com.web.stard.domain.member.application.impl;

import com.web.stard.domain.member.application.MemberService;
import com.web.stard.domain.member.domain.Interest;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.domain.Profile;
import com.web.stard.domain.member.domain.enums.InterestField;
import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.request.MemberRequestDto.SignInDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;
import com.web.stard.domain.member.repository.InterestRepository;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.global.config.aws.S3Manager;
import com.web.stard.global.config.security.JwtTokenProvider;
import com.web.stard.global.dto.TokenInfo;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import com.web.stard.global.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.UUID;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final RedisUtils redisUtils;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Manager s3Manager;
    private final AuthenticationManager authenticationManager;

    /**
     * 회원가입
     *
     * @param file              프로필 이미지 파일
     * @param requestDto        email, password, nickname
     *                          이메일    비밀번호    닉네임
     * @return SignupResultDto  memberId, createdAt
     *                          멤버 id    생성일시
     */
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

    /**
     * 이메일 중복 확인
     *
     * @param email     중복 체크할 이메일
     * @return boolean  이메일이 중복되지 않으면 true, 중복되면 false
     */
    @Override
    public boolean checkEmailDuplicate(String email) {
        return !memberRepository.existsByEmail(email);
    }

    /**
     * 닉네임 중복 확인
     *
     * @param nickname  중복 체크할 닉네임
     * @return boolean  닉네임이 중복되지 않으면 true, 중복되면 false
     */
    @Override
    public boolean checkNicknameDuplicate(String nickname) {
        return !memberRepository.existsByNickname(nickname);
    }


    /**
     * 회원가입 추가 정보 저장
     *
     * @param requestDto        memberId, interests
     *                          멤버 id    관심분야
     * @return AdditionalInfoResultDto  memberId, interests
     *                                  멤버 id    관심분야
     */
    @Override
    public MemberResponseDto.AdditionalInfoResultDto saveAdditionalInfo(MemberRequestDto.AdditionalInfoRequestDto requestDto) {
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 관심 분야 저장
        if (requestDto.getInterests() != null && !requestDto.getInterests().isEmpty()) {
            requestDto.getInterests().forEach(interest -> {
                Interest interestEntity = Interest.builder()
                        .interestField(InterestField.find(interest))
                        .member(member)
                        .build();
                interestRepository.save(interestEntity);
            });
        }

        // 회원 정보 저장
        memberRepository.save(member);

        return MemberResponseDto.AdditionalInfoResultDto.of(member);
    }


    /**
     * 마이페이지 - 개인정보 수정 기존 데이터 상세 조회
     *
     * @param memberId   사용자 고유 id
     * @return InfoDto   nickname, interests
     *                   닉네임     관심분야
     */
    @Override
    public MemberResponseDto.InfoDto getInfo(Long memberId) {
        // 회원 정보 반환
        Member info = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 관심분야 반환
        List<Interest> interests = interestRepository.findAllByMember(info);

        return MemberResponseDto.InfoDto.of(info, interests);
    }


    // TODO : 비밀번호 변경 : 현재 비밀번호랑 다를 경우 변경 불가능


    /**
     * 마이페이지 - 개인정보 수정 : 닉네임
     *
     * @param requestDto                사용자 고유 id, nickname 닉네임
     * @return EditNicknameResponseDto  nickname 닉네임, message 성공 메시지
     */
    @Override
    public MemberResponseDto.EditNicknameResponseDto editNickname(MemberRequestDto.EditNicknameDto requestDto) {
        // 회원 정보 반환
        Member info = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 닉네임 변경
        info.updateNickname(requestDto.getNickname());

        memberRepository.save(info);

        return MemberResponseDto.EditNicknameResponseDto.of(info.getNickname());
    }

    /**
     * 마이페이지 - 개인정보 수정 : 관심분야
     * 기존 관심분야 삭제 후 새로 삽입
     * @param requestDto : EditInterestDto  사용자 고유 id, interestField 관심분야
     * @return EditInterestResponseDto      interests 관심분야, message 성공 메시지
     *
     */
    @Override
    public MemberResponseDto.EditInterestResponseDto editInterest(MemberRequestDto.AdditionalInfoRequestDto requestDto) {
        // 회원 정보 반환
        Member info = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 기존 관심분야 삭제 후 새로 삽입
        List<Interest> interests = new ArrayList<>(info.getInterests());
        info.getInterests().removeAll(interests); // 부모와 관계 삭제

        List<Interest> interestList = new ArrayList<>();
        if (requestDto.getInterests() != null && !requestDto.getInterests().isEmpty()) {
            requestDto.getInterests().forEach(interest -> {
                Interest interestEntity = Interest.builder()
                        .interestField(InterestField.find(interest))
                        .member(info)
                        .build();
                interestList.add(interestEntity);
            });
            interestRepository.saveAll(interestList);
        }

        return MemberResponseDto.EditInterestResponseDto.of(interestList);
    }

    /**
     * 로그인
     *
     * @param request 로그인 정보
     * @return TokenInfo 토큰 정보
     */
    @Override
    @Transactional
    public TokenInfo signIn(SignInDto request) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
            redisUtils.setData(request.email(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime());
            return tokenInfo;
        } catch (BadCredentialsException e) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
    }

}
