package com.web.stard.domain.member.service.impl;

import com.web.stard.domain.chat.domain.entity.ChatMessage;
import com.web.stard.domain.chat.repository.ChatMessageRepository;
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
import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.post.repository.PostRepository;
import com.web.stard.domain.reply.domain.entity.Reply;
import com.web.stard.domain.reply.repository.ReplyRepository;
import com.web.stard.domain.report.repository.ReportRepository;
import com.web.stard.domain.starScrap.domain.enums.TableType;
import com.web.stard.domain.starScrap.repository.StarScrapRepository;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyApplicant;
import com.web.stard.domain.study.domain.entity.StudyMember;
import com.web.stard.domain.study.domain.enums.ProgressType;
import com.web.stard.domain.study.domain.enums.RecruitmentType;
import com.web.stard.domain.study.repository.StudyApplicantRepository;
import com.web.stard.domain.study.repository.StudyMemberRepository;
import com.web.stard.domain.study.repository.StudyRepository;
import com.web.stard.domain.teamBlog.domain.entity.Assignee;
import com.web.stard.domain.teamBlog.domain.entity.Evaluation;
import com.web.stard.domain.teamBlog.domain.entity.StudyPost;
import com.web.stard.domain.teamBlog.domain.entity.StudyPostFile;
import com.web.stard.domain.teamBlog.repository.AssigneeRepository;
import com.web.stard.domain.teamBlog.repository.EvaluationRepository;
import com.web.stard.domain.teamBlog.repository.StudyPostRepository;
import com.web.stard.global.config.aws.S3Manager;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import com.web.stard.global.utils.FileUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    @Value("${base.back-end.url}")
    private String backEndUrl;

    @Value("${file.path.profile}")
    private String profilePath;

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
    private final AssigneeRepository assigneeRepository;
    private final EvaluationRepository evaluationRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final FileUtils fileUtils;

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
     * @param member 로그인 사용자
     * @return InfoDto  nickname, interests
     * 닉네임     관심분야
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
     * @param requestDto nickname 닉네임
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
     *
     * @param requestDto : EditInterestDto  interestField 관심분야
     * @return EditInterestResponseDto      interests 관심분야, message 성공 메시지
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
        return MemberResponseDto.ProfileImageResponseDto.from(backEndUrl + fullMember.getProfile().getImgUrl());
    }

    @Override
    @Transactional
    public MemberResponseDto.ProfileImageResponseDto updateProfileImage(MultipartFile file, Member member) {
        // 기존 이미지 삭제
        Profile profile = memberRepository.findByIdWithProfile(member.getId()).getProfile();
        if (profile.getImgUrl() != null) {
            // TODO 임시로 로컬에서 삭제
//            s3Manager.deleteFile(profile.getImgUrl());  // S3에서 파일 삭제
            fileUtils.deleteFile(profile.getImgUrl());
            profile.deleteImageUrl();   // DB에서 이미지 url 삭제
        }

        // 새 이미지 업로드
        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            UUID uuid = UUID.randomUUID();
//            String keyName = s3Manager.generateProfileKeyName(uuid);
//            fileUrl = s3Manager.uploadFile(keyName, file);  // S3에 파일 업로드

            // TODO 임시로 로컬에 파일 저장
            String keyName = fileUtils.generateProfileKeyName(uuid);
            fileUrl = fileUtils.uploadFile(keyName, file);

            profile.updateImageUrl(fileUrl);    // DB 이미지 url 변경
        }

        try {
            return MemberResponseDto.ProfileImageResponseDto.from(backEndUrl + fileUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    @Transactional
    public void deleteProfileImage(Member member) {
        Profile profile = memberRepository.findByIdWithProfile(member.getId()).getProfile();
        if (profile.getImgUrl() != null) {
            // TODO 임시로 로컬에서 삭제
//            s3Manager.deleteFile(profile.getImgUrl());
            fileUtils.deleteFile(profile.getImgUrl());
            profile.deleteImageUrl();
        }
    }

    // 특정 회원과 관련된 모든 엔티티 삭제
    @Override
    public void deleteAllRelatedEntities(Member member, boolean isForced) {
        List<StudyMember> studyMembers = studyMemberRepository.findByMember(member);
        if (!isForced) {
            // 진행 중인 스터디가 있으면 탈퇴 불가능
            boolean hasInProgressStudy = studyMembers.stream()
                    .anyMatch(studyMember -> studyMember.getStudy().getProgressType() == ProgressType.IN_PROGRESS);

            if (hasInProgressStudy) {
                // 탈퇴 불가능
                throw new CustomException(ErrorCode.CANNOT_DELETE_FROM_IN_PROGRESS_STUDY);
            }
        }

        Member unknownMember = memberRepository.findByNickname("알수없음")   // 알수없는 사용자
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        StudyMember unknownStudyMember = studyMemberRepository.findByMember_NicknameAndStudyIsNull("알수없음")
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_MEMBER_NOT_FOUND));


        // 공감, 스크랩 삭제
        starScrapRepository.deleteAllByMember(member);


        // 게시글 삭제
        List<Post> posts = postRepository.findByMember(member);
        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .toList();

        List<Reply> replies = replyRepository.findAllByTargetIdInAndPostType(postIds, PostType.COMM);
        List<Long> replyIds = replies.stream()
                .map(Reply::getId)
                .toList();
        reportRepository.deleteAllByTargetIdInAndPostType(replyIds, PostType.REPLY); // 댓글의 신고도 삭제
        replyRepository.deleteAllByTargetIdInAndPostType(postIds, PostType.COMM); // 댓글 삭제

        starScrapRepository.deleteAllByTargetIdInAndTableType(postIds, TableType.POST); // 공감 삭제
        reportRepository.deleteAllByTargetIdInAndPostType(postIds, PostType.COMM); // 게시글 신고 있다면 삭제
        postRepository.deleteAllByMember(member);


        // studyPost 삭제
        List<StudyPost> studyPosts = studyPostRepository.findByStudyMember_Member(member);
        for (StudyPost studyPost : studyPosts) {
            if (studyPost.getFiles() != null && !studyPost.getFiles().isEmpty()) {
                // 파일 삭제
                List<String> fileUrls = studyPost.getFiles()
                        .stream()
                        .map(StudyPostFile::getFileUrl)
                        .toList();
                // TODO 임시로 로컬에서 파일 삭제
//                s3Manager.deleteFiles(fileUrls); // S3 파일 삭제
                fileUtils.deleteFiles(fileUrls);
            }

            replies = replyRepository.findAllByTargetIdAndPostType(studyPost.getId(), PostType.STUDYPOST);
            replyIds = replies.stream()
                    .map(Reply::getId)
                    .toList();
            reportRepository.deleteAllByTargetIdInAndPostType(replyIds, PostType.REPLY); // 댓글의 신고 삭제
            replyRepository.deleteAll(replies); // 댓글 삭제

            starScrapRepository.deleteAllByTargetIdAndTableType(studyPost.getId(), TableType.STUDYPOST); // 스크랩 삭제

            reportRepository.deleteAllByTargetIdAndPostType(studyPost.getId(), PostType.STUDYPOST); // 게시글 신고 삭제
        }
        studyPostRepository.deleteAllByStudyMember_Member(member);


        // 투두, 평가, 채팅 - 알수없음 사용자로 변경
        for (StudyMember studyMember : studyMembers) {
            // 투두 담당자 - 알수없음 사용자로 변경
            List<Assignee> assignees = assigneeRepository.findAllByStudyMember(studyMember);
            assignees.forEach(assignee -> assignee.updateMemberToDeleted(unknownStudyMember));

            // 탈퇴할 회원이 작성한 평가 - 알수없음 사용자로 변경
            List<Evaluation> evaluations = evaluationRepository.findByStudyMember(studyMember);
            evaluations.forEach(evaluation -> evaluation.updateMemberToDeleted(unknownStudyMember));

            // 탈퇴할 회원이 받은 평가 - 삭제
            evaluations = evaluationRepository.findByTarget(studyMember);
            evaluationRepository.deleteAll(evaluations);

            // 탈퇴할 회원이 작성한 채팅 - 알수없음 사용자로 변경
            List<ChatMessage> chatMessages = chatMessageRepository.findAllByStudyMember(studyMember);
            chatMessages.forEach(chatMessage -> chatMessage.updateMemberToDeleted(unknownStudyMember));

            studyMember.updateMemberToDeleted(unknownMember);
        }


        // 스터디 신청 삭제
        List<StudyApplicant> studyApplicants = studyApplicantRepository.findByMember(member);
        for (StudyApplicant studyApplicant : studyApplicants) {
            if (studyApplicant.getStudy().getRecruitmentType() == RecruitmentType.RECRUITING) {
                // 모집 중일때만 삭제
                studyApplicantRepository.delete(studyApplicant);
            } else { // 그 외 - 알수없음 사용자로 변경
                studyApplicant.updateMemberToDeleted(unknownMember);
            }
        }


        // 스터디 삭제 - 알수없음 사용자로 변경
        List<Study> studies = studyRepository.findByMember(member);
        for (Study study : studies) {
            if (study.getProgressType() == ProgressType.NOT_STARTED) { // 아직 진행 전 - 스터디 삭제
                replies = replyRepository.findAllByTargetIdAndPostType(study.getId(), PostType.STUDY);
                replyIds = replies.stream()
                        .map(Reply::getId)
                        .toList();
                reportRepository.deleteAllByTargetIdInAndPostType(replyIds, PostType.REPLY); // 댓글의 신고 삭제
                replyRepository.deleteAll(replies); // 댓글 삭제

                starScrapRepository.deleteAllByTargetIdAndTableType(study.getId(), TableType.STUDY); // 스크랩 삭제

                reportRepository.deleteAllByTargetIdAndPostType(study.getId(), PostType.STUDY); // 게시글 신고 삭제

                studyApplicantRepository.deleteAllByStudy(study); // 신청자 삭제

                studyRepository.delete(study); // 스터디 삭제
            } else { // 그 외 - 알수없음 사용자로 변경
                study.updateMemberToDeleted(unknownMember);
            }
        }


        // 댓글 삭제
        replyRepository.deleteAllByMember(member);

        // 관심사 삭제
        interestRepository.deleteAllByMember(member);

        // 신고 내역 삭제
        reportRepository.deleteAllByMember(member);

        // 프로필 삭제
        if (member.getProfile().getImgUrl() != null) {
            // TODO 임시로 로컬에서 삭제
//            s3Manager.deleteFile(member.getProfile().getImgUrl());  // S3에서 파일 삭제
            fileUtils.deleteFile(member.getProfile().getImgUrl());
        }
        profileRepository.deleteById(member.getProfile().getId());
    }

    /**
     * 비밀번호 재설정
     *
     * @param email    이메일
     * @param password 변경된 비밀번호
     */
    @Override
    @Transactional
    public void resetPassword(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        if (passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomException(ErrorCode.DUPLICATE_PASSWORD);
        }
        member.updatePassword(passwordEncoder.encode(password));
    }

    /**
     * 자기소개 수정
     *
     * @param member     회원 정보
     * @param requestDto 수정된 자기소개
     * @return EditIntroduceResponseDto
     */
    @Override
    @Transactional
    public MemberResponseDto.EditIntroduceResponseDto editIntroduce(Member member, MemberRequestDto.EditIntroduceDto requestDto) {
        member = memberRepository.findById(member.getId()).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Profile profile = member.getProfile();
        profile.updateIntroduce(requestDto.introduce());
        return MemberResponseDto.EditIntroduceResponseDto.of(requestDto.introduce());
    }

    /**
     * 개인 신뢰도 조회
     *
     * @param member 회원 정보
     * @return CredibilityResponseDto
     */
    @Override
    @Transactional
    public MemberResponseDto.CredibilityResponseDto getCredibility(Member member) {
        member = memberRepository.findById(member.getId()).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        double credibility = new BigDecimal(member.getProfile().getCredibility()).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return new MemberResponseDto.CredibilityResponseDto(credibility);
    }

    @Override
    public Resource getProfileImageFile(String image) {
        try {
            Path path = Paths.get(fileUtils.getUploadRootPath() + profilePath + "/" + image);
            Resource resource = new UrlResource(path.toUri());

            // 파일 존재 여부 확인
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("파일이 존재하지 않거나 읽을 수 없습니다: " + image);
            }
            return resource;
        } catch (Exception e) {
            throw new RuntimeException("파일이 존재하지 않거나 읽을 수 없습니다: " + image);
        }
    }

    @Override
    @Transactional
    public MemberResponseDto.MemberProfileDto getProfile(Member member) {
        member = memberRepository.findById(member.getId()).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return new MemberResponseDto.MemberProfileDto(member.getNickname(), member.getProfile().getIntroduce(),
                (member.getProfile().getImgUrl() == null) ? null : backEndUrl + member.getProfile().getImgUrl());
    }

    @Override
    @Transactional
    public MemberResponseDto.MemberProfileDto updateProfile(Member member, MultipartFile image, MemberRequestDto.EditProfileDto request) {
        member = memberRepository.findById(member.getId()).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        member.getProfile().updateIntroduce(request.introduce());

        if (member.getProfile().getImgUrl() != null) {
            fileUtils.deleteFile(member.getProfile().getImgUrl());
        }

        if (!image.isEmpty()) {
            String keyName = fileUtils.generateProfileKeyName(UUID.randomUUID());
            String fileUrl = fileUtils.uploadFile(keyName, image);
            member.getProfile().updateImageUrl(fileUrl);
        }

        return new MemberResponseDto.MemberProfileDto(member.getNickname(), member.getProfile().getIntroduce(),
               backEndUrl + member.getProfile().getImgUrl());
    }

    // 애플리케이션 실행 후 호출됨
    @PostConstruct
    public void initUnknownMember() {
        unknownMemberService.createUnknownMemberIfNotExist();
    }

}
