package com.web.stard.domain.report.service.impl;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.domain.enums.Role;
import com.web.stard.domain.member.repository.InterestRepository;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.domain.member.repository.ProfileRepository;
import com.web.stard.domain.post.domain.entity.Post;
import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.post.repository.PostRepository;
import com.web.stard.domain.reply.domain.entity.Reply;
import com.web.stard.domain.report.domain.enums.ReportReason;
import com.web.stard.domain.reply.repository.ReplyRepository;
import com.web.stard.domain.report.domain.dto.response.ReportResponseDto;
import com.web.stard.domain.report.domain.dto.resquest.ReportRequestDto;
import com.web.stard.domain.report.domain.entity.Report;
import com.web.stard.domain.report.repository.ReportRepository;
import com.web.stard.domain.report.service.ReportService;
import com.web.stard.domain.starScrap.domain.enums.ActType;
import com.web.stard.domain.starScrap.domain.enums.TableType;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final StudyRepository studyRepository;
    private final StudyPostRepository studyPostRepository;
    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final StarScrapRepository starScrapRepository;
    private final S3Manager s3Manager;
    private final InterestRepository interestRepository;
    private final ProfileRepository profileRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final StudyApplicantRepository studyApplicantRepository;

    // 관리자인지 확인
    private void isAdmin(Member member) {
        if (member.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
    }

    // 게시글 조회, 작성자 여부 확인
    private boolean isAuthor(PostType postType, Long postId, Member member) {
        return switch (postType) {
            case STUDY -> {
                Study study = studyRepository.findById(postId)
                        .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
                yield study.getMember().getId().equals(member.getId());
            }
            case STUDYPOST -> {
                StudyPost studyPost = studyPostRepository.findById(postId)
                        .orElseThrow(() -> new CustomException(ErrorCode.STUDY_POST_NOT_FOUND));
                yield studyPost.getStudyMember().getMember().getId().equals(member.getId());
            }
            case REPLY -> {
                Reply reply = replyRepository.findById(postId)
                        .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));
                yield reply.getMember().getId().equals(member.getId());
            }
            default -> {
                Post post = postRepository.findByIdAndPostType(postId, postType)
                        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
                yield post.getMember().getId().equals(member.getId());
            }
        };
    }

    // targetId와 postType에 맞는 content 조회
    private String getContentFromTargetId(Long targetId, PostType postType) {
        return switch (postType) {
            case STUDY -> studyRepository.findById(targetId).map(Study::getTitle).orElse("내용 없음");
            case STUDYPOST -> studyPostRepository.findById(targetId).map(StudyPost::getTitle).orElse("내용 없음");
            case REPLY -> replyRepository.findById(targetId).map(Reply::getContent).orElse("내용 없음");
            default -> postRepository.findById(targetId).map(Post::getTitle).orElse("내용 없음");
        };
    }

    // 글 존재 여부 확인 및 객체 반환
    private Object getValidatedTargetEntity(Long targetId, PostType postType) {
        return switch (postType) {
            case STUDY -> studyRepository.findById(targetId)
                    .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));
            case STUDYPOST -> studyPostRepository.findById(targetId)
                    .orElseThrow(() -> new CustomException(ErrorCode.STUDY_POST_NOT_FOUND));
            case REPLY -> replyRepository.findById(targetId)
                    .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));
            default -> postRepository.findById(targetId)
                    .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        };
    }

    /**
     * 신고
     * @param requestDto targetId 신고 대상 id, postType 신고 대상 글 타입, reportReason 신고 사유, customReason 기타 신고 사유
     * @return ReportDto reportId 신고 id, createdAt 생성일시
     */
    @Override
    @Transactional
    public ReportResponseDto.ReportDto createReport(ReportRequestDto.ReportDto requestDto, Member member) {
        PostType postType = PostType.fromString(requestDto.getPostType());
        ReportReason reportReason = ReportReason.fromString(requestDto.getReportReason());

        if (isAuthor(postType, requestDto.getTargetId(), member)) {
            throw new CustomException(ErrorCode.REPORT_NOT_ALLOWED_FOR_AUTHOR);
        }

        // 이미 신고했는지 확인
        boolean alreadyReported = reportRepository.existsByTargetIdAndPostTypeAndMember(requestDto.getTargetId(), postType, member);
        if (alreadyReported) {
            throw new CustomException(ErrorCode.REPORT_ALREADY_EXISTS);
        }

        // customReason 처리
        String customReason = null;
        if (reportReason == ReportReason.CUSTOM) {
            if (requestDto.getCustomReason() == null || requestDto.getCustomReason().isBlank()) {
                throw new CustomException(ErrorCode.CUSTOM_REASON_REQUIRED);
            }
            customReason = requestDto.getCustomReason();
        }

        Report report = reportRepository.save(
                Report.builder()
                        .targetId(requestDto.getTargetId())
                        .postType(postType)
                        .reportReason(reportReason)
                        .customReason(customReason)
                        .member(member)
                        .build()
        );
        return ReportResponseDto.ReportDto.from(report);
    }

    /**
     * 신고 목록 조회
     * @param page 조회할 페이지 번호
     * @return ReportListDto reports 신고 리스트, currentPage 현재 페이지, totalPages 전체 페이지 수, isLast 마지막 페이지 여부
     */
    @Override
    @Transactional(readOnly = true)
    public ReportResponseDto.ReportListDto getReportList(int page, Member member) {
        isAdmin(member);

        List<Object[]> result = reportRepository.findReportsWithCountAndPostTypeNative();

        List<ReportResponseDto.ReportDetailDto> reportDtos = result.stream()
                .map(row -> {
                    Long targetId = Long.valueOf(row[0].toString());
                    PostType postType = PostType.valueOf(row[2].toString());
                    String content = getContentFromTargetId(targetId, postType);

                    return ReportResponseDto.ReportDetailDto.builder()
                            .reportId(targetId)
                            .reportCount(Integer.parseInt(row[1].toString()))
                            .content(content)
                            .postType(postType)
                            .build();
                })
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        int start = (page - 1) * 10;
        int end = Math.min(start + 10, reportDtos.size());
        
        List<ReportResponseDto.ReportDetailDto> pagedReportDtos = reportDtos.subList(start, end);
        Page<ReportResponseDto.ReportDetailDto> pagedReportDtosPage = new PageImpl<>(pagedReportDtos, pageable, reportDtos.size());

        return ReportResponseDto.ReportListDto.of(pagedReportDtosPage);
    }

    /**
     * 신고 사유 조회
     * @param targetId 조회할 글 id
     * @return ReportReasonListDto reportReasons 일반 신고 사유 목록, customReasons 커스텀 사유 목록
     */
    @Override
    @Transactional(readOnly = true)
    public ReportResponseDto.ReportReasonListDto getReportReasonList(Long targetId, Member member) {
        isAdmin(member);

        List<Report> reports = reportRepository.findByTargetId(targetId);

        // 신고 사유별로 그룹화, 해당 사유의 개수 포함
        Map<ReportReason, Long> reasonCounts = reports.stream()
                .collect(Collectors.groupingBy(Report::getReportReason, Collectors.counting()));

        List<ReportResponseDto.ReportReasonDto> reportReasons = reasonCounts.entrySet().stream()
                .filter(entry -> entry.getKey() != ReportReason.CUSTOM)
                .map(entry -> ReportResponseDto.ReportReasonDto.builder()
                        .reason(entry.getKey().getDescription())
                        .count(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        // CUSTOM 사유
        List<String> customReasons = reports.stream()
                .filter(report -> report.getReportReason() == ReportReason.CUSTOM)
                .map(Report::getCustomReason)
                .collect(Collectors.toList());

        return ReportResponseDto.ReportReasonListDto.builder()
                .reportReasons(reportReasons)  // 일반 신고 사유 목록
                .customReasons(customReasons)  // 모든 customReason 목록
                .build();
    }

    /**
     * 신고 승인
     *
     * @param targetId 신고 승인할 글 id
     * @param postType 글 타입
     * @return ReportProcessDto targetId 신고 승인된 글 id, message 처리 결과
     */
    @Override
    @Transactional
    public ReportResponseDto.ReportProcessDto approveReport(Long targetId, String postType, Member member) {
        isAdmin(member);

        PostType type = PostType.fromString(postType);
        Object targetEntity = getValidatedTargetEntity(targetId, type);

        // 신고 수 증가 및 해당 글 삭제
        if (targetEntity instanceof Study study) {  // 스터디 - 댓글, 스타 삭제
            study.getMember().increaseReportCount();
            replyRepository.deleteAllByTargetIdAndPostType(targetId, type);
            starScrapRepository.deleteByActTypeAndTableTypeAndTargetId(ActType.STAR, TableType.STUDY, study.getId());
            studyRepository.delete(study);
        } else if (targetEntity instanceof StudyPost studyPost) {   // studyPost - 댓글, 파일, 스크랩 삭제
            studyPost.getStudyMember().getMember().increaseReportCount();
            replyRepository.deleteAllByTargetIdAndPostType(targetId, type);
            if (studyPost.getFiles() != null) {
                List<String> fileUrls = studyPost.getFiles().stream().map(StudyPostFile::getFileUrl).toList();
                s3Manager.deleteFiles(fileUrls);
            }
            starScrapRepository.deleteByActTypeAndTableTypeAndTargetId(ActType.SCRAP, TableType.STUDYPOST, studyPost.getId());
            studyPostRepository.delete(studyPost);
        } else if (targetEntity instanceof Reply reply) {   // 댓글
            reply.getMember().increaseReportCount();
            replyRepository.delete(reply);
        } else if (targetEntity instanceof Post post) {     // 게시글 - 댓글, 스타 삭제
            post.getMember().increaseReportCount();
            replyRepository.deleteAllByTargetIdAndPostType(targetId, type);
            starScrapRepository.deleteByActTypeAndTableTypeAndTargetId(ActType.STAR, TableType.POST, post.getId());
            postRepository.delete(post);
        } else {
            throw new CustomException(ErrorCode.REPORT_PROCESS_ERROR);
        }

        // 신고 내역 삭제
        reportRepository.deleteByTargetIdAndPostType(targetId, PostType.fromString(postType));

        return ReportResponseDto.ReportProcessDto.builder()
                .targetId(targetId)
                .message("신고가 승인되었습니다.")
                .build();
    }

    /**
     * 신고 반려
     *
     * @param targetId 신고 반려할 글 id
     * @param postType 글 타입
     * @return ReportProcessDto targetId 신고 승인된 글 id, message 처리 결과
     */
    @Override
    @Transactional
    public ReportResponseDto.ReportProcessDto rejectReport(Long targetId, String postType, Member member) {
        isAdmin(member);

        PostType type = PostType.fromString(postType);
        getValidatedTargetEntity(targetId, type);

        reportRepository.deleteByTargetIdAndPostType(targetId, PostType.fromString(postType));

        return ReportResponseDto.ReportProcessDto.builder()
                .targetId(targetId)
                .message("신고가 반려되었습니다.")
                .build();
    }

    /**
     * 누적 신고 수가 1 이상인 회원 목록 조회
     * @param page 조회할 페이지 번호
     * @return ReportMemberListDto members 회원 리스트, currentPage 현재 페이지, totalPages 전체 페이지 수, isLast 마지막 페이지 여부
     */
    @Override
    @Transactional(readOnly = true)
    public ReportResponseDto.ReportMemberListDto getReportedMemberList(int page, Member member) {
        isAdmin(member);

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        Page<Member> reportedMembers = memberRepository.findByReportCountGreaterThanEqual(1, pageable);

        Page<ReportResponseDto.ReportMember> reportMemberDto = reportedMembers.map(m ->
                ReportResponseDto.ReportMember.builder()
                        .memberId(m.getId())
                        .nickname(m.getNickname())
                        .reportCount(m.getReportCount())
                        .profileImg(m.getProfile().getImgUrl())
                        .build()
        );

        return ReportResponseDto.ReportMemberListDto.of(reportMemberDto);
    }

    /**
     * 회원 강제 탈퇴
     *
     * @param memberId 강제 탈퇴할 회원 id
     * @return Long 강제 탈퇴한 회원 id
     */
    @Override
    @Transactional
    public ReportResponseDto.ForceDeleteDto forceDeleteMember(Long memberId, Member member) {
        isAdmin(member);

        Member deleteMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        deleteAllRelatedEntities(deleteMember);
        memberRepository.delete(deleteMember);

        return ReportResponseDto.ForceDeleteDto.builder()
                .deletedMemberId(deleteMember.getId())
                .message("탈퇴 처리되었습니다.")
                .build();
    }

    // 특정 회원과 관련된 모든 엔티티 삭제
    @Override
    public void deleteAllRelatedEntities(Member member) {
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
}
