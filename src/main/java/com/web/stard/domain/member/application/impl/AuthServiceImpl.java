package com.web.stard.domain.member.application.impl;

import com.web.stard.domain.member.application.AuthService;
import com.web.stard.domain.member.domain.Interest;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.domain.Profile;
import com.web.stard.domain.member.domain.enums.InterestField;
import com.web.stard.domain.member.dto.request.MemberRequestDto;
import com.web.stard.domain.member.dto.response.MemberResponseDto;
import com.web.stard.domain.member.repository.InterestRepository;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.global.config.aws.S3Manager;
import com.web.stard.global.config.security.JwtTokenProvider;
import com.web.stard.global.dto.TokenInfo;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import com.web.stard.global.utils.EmailUtils;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Manager s3Manager;
    private final RedisUtils redisUtils;
    private final EmailUtils emailUtils;
    private final JwtTokenProvider jwtTokenProvider;
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
    @Transactional
    public MemberResponseDto.SignupResultDto signUp(MultipartFile file, MemberRequestDto.SignupDto requestDto) {

        if (!checkEmailDuplicate(requestDto.getEmail())) {
           throw new CustomException(ErrorCode.EMAIL_CONFLICT);
        };
        if (!checkNicknameDuplicate(requestDto.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_CONFLICT);
        }

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
    @Transactional
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
     * 로그인
     *
     * @param request 로그인 정보
     * @return TokenInfo 토큰 정보
     */
    @Override
    @Transactional
    public TokenInfo signIn(MemberRequestDto.SignInDto request) {
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

    @Override
    public void sendAuthCode(String email) throws Exception {
        emailUtils.send(email);
    }

    @Override
    public void validAuthCode(MemberRequestDto.AuthCodeRequestDto request) throws Exception {
        emailUtils.validAuthCode(request.email(), request.authCode());
    }

}