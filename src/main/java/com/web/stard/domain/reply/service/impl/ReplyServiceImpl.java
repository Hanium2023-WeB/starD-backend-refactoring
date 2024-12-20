package com.web.stard.domain.reply.service.impl;

import com.web.stard.domain.reply.service.ReplyService;
import com.web.stard.domain.reply.domain.entity.Reply;
import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.reply.domain.dto.request.ReplyRequestDto;
import com.web.stard.domain.reply.domain.dto.response.ReplyResponseDto;
import com.web.stard.domain.post.repository.PostRepository;
import com.web.stard.domain.reply.repository.ReplyRepository;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.domain.study.repository.StudyRepository;
import com.web.stard.domain.teamBlog.repository.StudyPostRepository;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final StudyRepository studyRepository;
    private final StudyPostRepository studyPostRepository;

    // 게시글 존재 여부 확인
    private void validatePostExists(Long targetId, PostType postType) {
        boolean postExists = switch (postType) {
            case STUDY -> studyRepository.existsById(targetId);
            case STUDYPOST -> studyPostRepository.existsById(targetId);
            default -> postRepository.existsById(targetId);
        };

        // 게시글이 존재하지 않으면 에러 발생
        if (!postExists) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }
    }

    // 댓글 찾기
    private Reply findReply(Long id) {
        return replyRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));
    }

    // 작성자인지 확인
    private boolean isReplyAuthor(Member member, Reply reply) {
        if (!member.getId().equals(reply.getMember().getId())) {
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }
        return true;
    }

    /**
     * 댓글 생성
     *
     * @param targetId 댓글 작성할 게시글 id
     * @param requestDto content 내용, type 게시글 타입
     * @return ReplyDto replyId 댓글 id, content 내용, writer 작성자, profileImg 프로필 이미지, updatedAt 수정일시
     */
    @Override
    @Transactional
    public ReplyResponseDto.ReplyDto createReply(Long targetId, ReplyRequestDto.CreateReplyDto requestDto, Member member) {
        PostType postType = PostType.fromString(requestDto.getType());
        validatePostExists(targetId, postType);

        Reply reply = replyRepository.save(requestDto.toEntity(member, targetId, postType));
        Member writer = memberRepository.findById(member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return ReplyResponseDto.ReplyDto.from(reply, writer, true);
    }

    /**
     * 댓글 수정
     *
     * @param replyId 댓글 작성할 게시글 id
     * @param requestDto content 내용
     * @return ReplyDto replyId 댓글 id, content 내용, writer 작성자, profileImg 프로필 이미지, updatedAt 수정일시
     */
    @Override
    @Transactional
    public ReplyResponseDto.ReplyDto updateReply(Long replyId, ReplyRequestDto.UpdateReplyDto requestDto, Member member) {
        Reply reply = findReply(replyId);
        boolean isAuthor = isReplyAuthor(member, reply);
        validatePostExists(reply.getTargetId(), reply.getPostType());

        reply.updateReply(requestDto.getContent());
        return ReplyResponseDto.ReplyDto.from(reply, reply.getMember(), isAuthor);
    }

    /**
     * 댓글 삭제
     * @param replyId 댓글 삭제할 게시글 id
     * @return Long 삭제한 댓글 id
     */
    @Override
    @Transactional
    public Long deleteReply(Long replyId, Member member) {
        Reply reply = findReply(replyId);
        isReplyAuthor(member, reply);
        validatePostExists(reply.getTargetId(), reply.getPostType());

        replyRepository.delete(reply);
        return replyId;
    }

    /**
     * 댓글 전체 조회
     * @param targetId 댓글 조회할 게시글 id
     * @param type  게시글 타입
     * @param page 조회할 페이지 번호
     * @return ReplyListDto replies 댓글 리스트, currentPage 현재 페이지, totalPages 전체 페이지, isLast 마지막 페이지 여부
     */
    @Override
    @Transactional(readOnly = true)
    public ReplyResponseDto.ReplyListDto getReplyList(Long targetId, String type, int page, Member member) {
        validatePostExists(targetId, PostType.fromString(type));

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "createdAt"));   // 최신 순
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        Page<Reply> replies = replyRepository.findByTargetIdAndPostType(targetId, PostType.fromString(type), pageable);

        List<ReplyResponseDto.ReplyDto> replyDtos = replies.getContent().stream()
                .map(reply -> {
                    boolean isAuthor = (member != null && reply.getMember().getId().equals(member.getId()));
                    return ReplyResponseDto.ReplyDto.from(reply, reply.getMember(), isAuthor);
                })
                .toList();

        return ReplyResponseDto.ReplyListDto.of(replies, replyDtos);
    }


    /**
     * 사용자가 작성한 댓글 전체 조회
     * @param page 조회할 페이지 번호
     * @return ReplyListDto replies 댓글 리스트, currentPage 현재 페이지, totalPages 전체 페이지, isLast 마지막 페이지 여부
     */
    @Override
    @Transactional(readOnly = true)
    public ReplyResponseDto.MyPageReplyListDto getMemberReplyList(int page, Member member) {
        Member user = memberRepository.findById(member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "createdAt"));   // 최신 순
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        Page<Reply> replies = replyRepository.findAllByMember(member, pageable);

        return ReplyResponseDto.MyPageReplyListDto.of(replies, user);
    }
}
