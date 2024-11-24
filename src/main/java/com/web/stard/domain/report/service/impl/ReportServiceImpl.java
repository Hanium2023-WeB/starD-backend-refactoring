package com.web.stard.domain.report.service.impl;

import com.web.stard.domain.member.domain.entity.Member;
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
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.repository.StudyRepository;
import com.web.stard.domain.teamBlog.domain.entity.StudyPost;
import com.web.stard.domain.teamBlog.repository.StudyPostRepository;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final StudyRepository studyRepository;
    private final StudyPostRepository studyPostRepository;
    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;

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

}
