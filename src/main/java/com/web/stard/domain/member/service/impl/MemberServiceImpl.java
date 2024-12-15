package com.web.stard.domain.member.service.impl;

import com.web.stard.domain.member.service.MemberService;
import com.web.stard.domain.member.domain.entity.Interest;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.domain.entity.Profile;
import com.web.stard.domain.member.domain.enums.InterestField;
import com.web.stard.domain.member.domain.dto.request.MemberRequestDto;
import com.web.stard.domain.member.domain.dto.response.MemberResponseDto;
import com.web.stard.domain.member.repository.InterestRepository;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.global.config.aws.S3Manager;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Iterator;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Manager s3Manager;
    private final UnknownMemberService unknownMemberService;

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
     * @param member    로그인 사용자
     * @return InfoDto  nickname, interests
     *                   닉네임     관심분야
     */
    @Transactional(readOnly = true)
    @Override
    public MemberResponseDto.InfoDto getInfo(Member member) {
        // 관심분야 반환
        List<Interest> interests = interestRepository.findAllByMember(member);
        return MemberResponseDto.InfoDto.of(member, interests);
    }

    /**
     * 마이페이지 - 개인정보 수정 : 비밀번호
     *
     * @param requestDto originPassword 현재 비밀번호, password 바꿀 비밀번호
     * @return 없음
     */
    @Transactional
    @Override
    public ResponseEntity<String> editPassword(Member member, MemberRequestDto.EditPasswordDto requestDto) {
        // 현재 비밀번호 확인
        if (!checkCurrentPassword(member.getPassword(), requestDto.getOriginPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        member.updatePassword(encodedPassword);

        memberRepository.save(member);

        return ResponseEntity.status(HttpStatus.OK).body("비밀번호를 변경하였습니다.");
    }

    /**
     * 마이페이지 - 개인정보 수정 : 닉네임
     *
     * @param requestDto                nickname 닉네임
     * @return EditNicknameResponseDto  nickname 닉네임, message 성공 메시지
     */
    @Transactional
    @Override
    public MemberResponseDto.EditNicknameResponseDto editNickname(Member member, MemberRequestDto.EditNicknameDto requestDto) {
        // 닉네임 중복 확인
        if (memberRepository.existsByNickname(requestDto.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_CONFLICT);
        }
        
        // 닉네임 변경
        member.updateNickname(requestDto.getNickname());

        memberRepository.save(member);

        return MemberResponseDto.EditNicknameResponseDto.of(member.getNickname());
    }

    /**
     * 마이페이지 - 개인정보 수정 : 관심분야
     * 기존 관심분야와 비교 - 삭제, 추가
     * @param requestDto : EditInterestDto  interestField 관심분야
     * @return EditInterestResponseDto      interests 관심분야, message 성공 메시지
     *
     */
    @Transactional
    @Override
    public MemberResponseDto.EditInterestResponseDto editInterest(Member member, MemberRequestDto.AdditionalInfoRequestDto requestDto) {
        // 회원 정보 반환
        Member info = memberRepository.findById(member.getId())
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

    @Override
    @Transactional(readOnly = true)
    public MemberResponseDto.ProfileImageResponseDto getProfileImage(Member member) {
        Member fullMember = memberRepository.findByIdWithProfile(member.getId());
        return MemberResponseDto.ProfileImageResponseDto.from(fullMember.getProfile().getImgUrl());
    }

    @Override
    @Transactional
    public MemberResponseDto.ProfileImageResponseDto updateProfileImage(MultipartFile file, Member member) {
        // 기존 이미지 삭제
        Profile profile = memberRepository.findByIdWithProfile(member.getId()).getProfile();
        if (profile.getImgUrl() != null) {
            s3Manager.deleteFile(profile.getImgUrl());  // S3에서 파일 삭제
            profile.deleteImageUrl();   // DB에서 이미지 url 삭제
        }

        // 새 이미지 업로드
        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String keyName = s3Manager.generateProfileKeyName(uuid);

            fileUrl = s3Manager.uploadFile(keyName, file);  // S3에 파일 업로드
            profile.updateImageUrl(fileUrl);    // DB 이미지 url 변경
        }

        return MemberResponseDto.ProfileImageResponseDto.from(fileUrl);
    }

    @Override
    @Transactional
    public void deleteProfileImage(Member member) {
        Profile profile = memberRepository.findByIdWithProfile(member.getId()).getProfile();
        if (profile.getImgUrl() != null) {
            s3Manager.deleteFile(profile.getImgUrl());
            profile.deleteImageUrl();
        }
    }


    // 애플리케이션 실행 후 호출됨
    @PostConstruct
    public void initUnknownMember() {
        unknownMemberService.createUnknownMemberIfNotExist();
    }

//    @Transactional
//    public void sendEmailResetPw(MemberRequestDto.EmailRequestDto request) throws MessagingException {
//
//        Member member = memberService.findByEmail(emailDto.getEmail());
//
//        String pwResetToken = UUID.randomUUID().toString();
//
//        boolean isExist = true;
//
//        while(isExist){
//
//            String existEmail = redisUtil.getData(RESET_PW_PREFIX + pwResetToken);
//            if (existEmail == null)
//                break;
//            else
//                pwResetToken = UUID.randomUUID().toString();
//        }
//
//        String pwResetUrl = baseUrl + "/reset-password?token=" + pwResetToken;
//
//        MimeMessage message = emailSender.createMimeMessage();
//        message.addRecipients(MimeMessage.RecipientType.TO, emailDto.getEmail());
//        message.setSubject(RESET_PW_SUBJECT);
//
//        String messageContent = "<h2>비밀번호 재설정 안내 </h2> <br>" +
//                "<p>안녕하세요. " + member.getId() +" 님</p>" +
//                "<p>본 메일은 비밀번호 재설정을 위해 StarD에서 발송하는 메일입니다. 12시간 이내에 " +
//                "링크를 클릭하여 비밀번호 재설정을 완료해주세요.</p>" +
//                "<a href=\"" + pwResetUrl + "\">비밀번호 재설정</a>";
//
//        message.setText(messageContent, "UTF-8", "html");
//        String sender = adminAccount + "@naver.com";
//        message.setFrom(new InternetAddress(sender));
//
//        try {
//            emailSender.send(message);
//            redisUtil.setData(RESET_PW_PREFIX + pwResetToken, emailDto.getEmail(), RESET_PW_TOKEN_EXPIRE_TIME);
//        } catch (MailException e) {
//            e.printStackTrace();
//            log.debug("MailService.sendEmail exception occur toEmail: {}", emailDto.getEmail());
//            throw new IllegalArgumentException();
//        }
//    }

}
