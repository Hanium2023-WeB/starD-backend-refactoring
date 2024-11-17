package com.web.stard.domain.board.global.service.impl;

import com.web.stard.domain.board.global.service.ReplyService;
import com.web.stard.domain.board.global.domain.Reply;
import com.web.stard.domain.board.global.domain.enums.PostType;
import com.web.stard.domain.board.global.dto.request.ReplyRequestDto;
import com.web.stard.domain.board.global.dto.response.ReplyResponseDto;
import com.web.stard.domain.board.global.repository.PostRepository;
import com.web.stard.domain.board.global.repository.ReplyRepository;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.domain.study.repository.StudyRepository;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final StudyRepository studyRepository;
//    private final StudyPostRepository studyPostRepository;

    // TODO: StudyPost 구현 후 관련 주석 해제
    // 게시글 존재 여부 확인
    private void validatePostExists(Long targetId, PostType postType) {
        boolean postExists = switch (postType) {
            case STUDY -> studyRepository.existsById(targetId);
//            case STUDYPOST -> studyPostRepository.existsById(targetId);
            default -> postRepository.existsById(targetId);
        };

        // 게시글이 존재하지 않으면 에러 발생
        if (!postExists) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }
    }

    // id, 작성자로 댓글 찾기
    private Reply findReply(Long id, Member member) {
        return replyRepository.findByIdAndMember(id, member)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));
    }

    // 타입 변환 (string to enum)
    private PostType convertType(String type) {
        if (type == null || type.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_POST_TYPE);
        }

        return switch (type.toLowerCase()) {
            case "study" -> PostType.STUDY;
            case "studypost" -> PostType.STUDYPOST;
            case "comm" -> PostType.COMM;
            case "qna" -> PostType.QNA;
            default -> throw new CustomException(ErrorCode.INVALID_POST_TYPE);
        };
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
        PostType postType = convertType(requestDto.getType());
        validatePostExists(targetId, postType);

        Reply reply = replyRepository.save(requestDto.toEntity(member, targetId, postType));
        Member writer = memberRepository.findById(member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return ReplyResponseDto.ReplyDto.from(reply, writer);
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
        Reply reply = findReply(replyId, member);
        validatePostExists(reply.getTargetId(), reply.getPostType());

        reply.updateReply(requestDto.getContent());
        return ReplyResponseDto.ReplyDto.from(reply, reply.getMember());
    }

    /**
     * 댓글 삭제
     * @param replyId 댓글 삭제할 게시글 id
     * @return Long 삭제한 댓글 id
     */
    @Override
    @Transactional
    public Long deleteReply(Long replyId, Member member) {
        Reply reply = findReply(replyId, member);
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
        validatePostExists(targetId, convertType(type));

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "createdAt"));   // 최신 순
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        Page<Reply> replies = replyRepository.findByTargetId(targetId, pageable);
        return ReplyResponseDto.ReplyListDto.of(replies);
    }
}