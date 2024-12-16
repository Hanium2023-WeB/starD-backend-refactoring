package com.web.stard.domain.member.service.impl;

import com.web.stard.domain.member.service.AuthService;
import com.web.stard.domain.member.domain.entity.Interest;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.domain.entity.Profile;
import com.web.stard.domain.member.domain.enums.InterestField;
import com.web.stard.domain.member.domain.dto.request.MemberRequestDto;
import com.web.stard.domain.member.domain.dto.response.MemberResponseDto;
import com.web.stard.domain.member.repository.InterestRepository;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.global.config.aws.S3Manager;
import com.web.stard.global.config.security.JwtTokenProvider;
import com.web.stard.global.dto.TokenInfo;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import com.web.stard.global.utils.CookieUtils;
import com.web.stard.global.utils.EmailUtils;
import com.web.stard.global.utils.RedisUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
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
    private final CookieUtils cookieUtils;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    private static final String RESET_PW_PREFIX = "ResetPwToken ";

    /**
     * 회원가입
     *
     * @param file       프로필 이미지 파일
     * @param requestDto email, password, nickname
     *                   이메일    비밀번호    닉네임
     * @return SignupResultDto  memberId, createdAt
     * 멤버 id    생성일시
     */
    @Override
    @Transactional
    public MemberResponseDto.SignupResultDto signUp(MultipartFile file, MemberRequestDto.SignupDto requestDto) {

        if (!checkEmailDuplicate(requestDto.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_CONFLICT);
        }

        if (!checkNicknameDuplicate(requestDto.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_CONFLICT);
        }

        if (requestDto.getEmail().equals("unknown@stard.com")) {
            throw new CustomException(ErrorCode.INVALID_EMAIL);
        }

        if (requestDto.getNickname().equals("알수없음")) {
            throw new CustomException(ErrorCode.INVALID_NICKNAME);
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
     * @param email 중복 체크할 이메일
     * @return boolean  이메일이 중복되지 않으면 true, 중복되면 false
     */
    @Override
    public boolean checkEmailDuplicate(String email) {
        return !memberRepository.existsByEmail(email);
    }

    /**
     * 닉네임 중복 확인
     *
     * @param nickname 중복 체크할 닉네임
     * @return boolean  닉네임이 중복되지 않으면 true, 중복되면 false
     */
    @Override
    public boolean checkNicknameDuplicate(String nickname) {
        return !memberRepository.existsByNickname(nickname);
    }


    /**
     * 회원가입 추가 정보 저장
     *
     * @param requestDto memberId, interests
     *                   멤버 id    관심분야
     * @return AdditionalInfoResultDto  memberId, interests
     * 멤버 id    관심분야
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
    public TokenInfo signIn(MemberRequestDto.SignInDto request, HttpServletResponse response) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
            redisUtils.setData(request.email(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime());
            cookieUtils.generateRefreshTokenCookie(response, tokenInfo);
            return tokenInfo;
        } catch (BadCredentialsException e) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
    }

    /**
     * 이메일로 인증 번호 보내기
     *
     * @param email 이메일
     * @throws Exception 이메일 전송 에러
     */
    @Override
    public void sendAuthCode(String email) throws Exception {
        emailUtils.send(email);
    }

    /**
     * 이메일 검증
     *
     * @param request 이메일과 인증 번호
     * @throws Exception 이메일 또는 인증 번호 불알치 / 인증 번호 유효 시간 만료
     */
    @Override
    public void validAuthCode(MemberRequestDto.AuthCodeRequestDto request) throws Exception {
        emailUtils.validAuthCode(request.email(), request.authCode());
    }

    /**
     * 로그아웃
     *
     * @param member      로그인한 사용자
     * @param accessToken 헤더에 전송된 토큰
     */
    @Override
    public void signOut(Member member, String accessToken, HttpServletResponse response) {
        try {
            jwtTokenProvider.validateToken(accessToken);
            String refreshToken = redisUtils.getData(member.getEmail());
            if (Objects.nonNull(refreshToken)) {
                redisUtils.deleteData(member.getEmail());
                cookieUtils.deleteRefreshTokenCookie(response, refreshToken);
            }
            redisUtils.setData(accessToken, "signOut", jwtTokenProvider.getExpiration(accessToken));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * JWT 토큰 재발급
     *
     * @param response
     * @param request
     * @return TokenInfo
     */
    @Override
    public TokenInfo reissue(HttpServletResponse response, HttpServletRequest request) {
        String refreshToken = cookieUtils.getCookie(request);
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }
        jwtTokenProvider.validateToken(refreshToken);
        String username = jwtTokenProvider.parseClaims(refreshToken).getSubject();
        if (redisUtils.getData(username) == null) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        cookieUtils.generateRefreshTokenCookie(response, tokenInfo);
        return tokenInfo;
    }

    /**
     * 비밀번호 찾기
     *
     * @param email 이메일
     */
    @Override
    public void findPassword(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        String pwResetToken = UUID.randomUUID().toString();
        emailUtils.sendPwResetUrl(member.getEmail(), pwResetToken);
    }

    /**
     * 비밀번호 재설정 토큰 검증
     *
     * @param token 비밀번호 재설정 토큰
     */
    @Override
    public String validPasswordResetToken(String token) {
        String email = redisUtils.getData(RESET_PW_PREFIX + token);
        if (email == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        return email;
    }

}
