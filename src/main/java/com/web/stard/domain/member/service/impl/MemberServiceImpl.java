package com.web.stard.domain.member.service.impl;

import com.web.stard.domain.member.repository.ProfileRepository;
import com.web.stard.domain.member.service.MemberService;
import com.web.stard.domain.member.domain.entity.Interest;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.domain.entity.Profile;
import com.web.stard.domain.member.domain.enums.InterestField;
import com.web.stard.domain.member.domain.dto.request.MemberRequestDto;
import com.web.stard.domain.member.domain.dto.response.MemberResponseDto;
import com.web.stard.domain.member.repository.InterestRepository;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.domain.post.domain.entity.Post;
import com.web.stard.domain.post.repository.PostRepository;
import com.web.stard.domain.reply.repository.ReplyRepository;
import com.web.stard.domain.report.repository.ReportRepository;
import com.web.stard.domain.starScrap.repository.StarScrapRepository;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.repository.StudyApplicantRepository;
import com.web.stard.domain.study.repository.StudyMemberRepository;
import com.web.stard.domain.study.repository.StudyRepository;
import com.web.stard.domain.teamBlog.domain.entity.StudyPost;
import com.web.stard.domain.teamBlog.domain.entity.StudyPostFile;
import com.web.stard.domain.teamBlog.repository.StudyPostRepository;
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

    private final ProfileRepository profileRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final StudyApplicantRepository studyApplicantRepository;
    private final ReportRepository reportRepository;
    private final StudyRepository studyRepository;
    private final StudyPostRepository studyPostRepository;
    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;
    private final StarScrapRepository starScrapRepository;

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

    /**
     * 회원 탈퇴
     *
     * @param member 회원
     * @return Long 탈퇴한 회원 고유 id, message
     */
    @Override
    @Transactional
    public MemberResponseDto.DeleteDto deleteMember(Member member) {
        Member deleteMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        deleteAllRelatedEntities(member, false);
        memberRepository.delete(deleteMember);

        return MemberResponseDto.DeleteDto.builder()
                .deletedMemberId(deleteMember.getId())
                .message("탈퇴 처리되었습니다.")
                .build();
    }

    // 특정 회원과 관련된 모든 엔티티 삭제
    @Override
    public void deleteAllRelatedEntities(Member member, boolean isForced) {
        Member unknownMember = memberRepository.findByNickname("알수없음")   // 알수없는 사용자
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 스타, 스크랩 삭제
        starScrapRepository.deleteAllByMember(member);

        // 게시글 삭제
        List<Post> posts = postRepository.findByMember(member);
        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .toList();
        replyRepository.deleteAllByTargetIdIn(postIds);
        postRepository.deleteAllByMember(member);

        // studyPost 삭제
        List<StudyPost> studyPosts = studyPostRepository.findByStudyMember_Member(member);
        for (StudyPost studyPost : studyPosts) {
            if (studyPost.getFiles() != null && !studyPost.getFiles().isEmpty()) {
                List<String> fileUrls = studyPost.getFiles()
                        .stream()
                        .map(StudyPostFile::getFileUrl)
                        .toList();
                s3Manager.deleteFiles(fileUrls); // S3 파일 삭제
            }
        }
        studyPostRepository.deleteAllByStudyMember_Member(member);

        // 스터디 삭제 - 알수없음 사용자로 변경
        List<Study> studies = studyRepository.findByMember(member);
        studies.forEach(study -> study.updateMemberToDeleted(unknownMember));
        studyMemberRepository.deleteByMember(member);
        studyApplicantRepository.deleteByMember(member);

        // 댓글 삭제
        replyRepository.deleteAllByMember(member);

        // 관심사 삭제
        interestRepository.deleteAllByMember(member);

        // 신고 내역 삭제
        reportRepository.deleteAllByMember(member);

        // 프로필 삭제
        if (member.getProfile().getImgUrl() != null) {
            s3Manager.deleteFile(member.getProfile().getImgUrl());  // S3에서 파일 삭제
        }
        profileRepository.deleteById(member.getProfile().getId());
    }

    // 애플리케이션 실행 후 호출됨
    @PostConstruct
    public void initUnknownMember() {
        unknownMemberService.createUnknownMemberIfNotExist();
    }

}
