package com.web.stard.domain.board.global.application.impl;

import com.web.stard.domain.board.global.application.StarScrapService;
import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.domain.StarScrap;
import com.web.stard.domain.board.global.domain.enums.ActType;
import com.web.stard.domain.board.global.domain.enums.TableType;
import com.web.stard.domain.board.global.repository.PostRepository;
import com.web.stard.domain.board.global.repository.StarScrapRepository;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.domain.enums.Role;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StarScrapServiceImpl implements StarScrapService {

    private final StarScrapRepository starScrapRepository;
    private final PostRepository postRepository;


    // 해당 게시글의 공감 혹은 스크랩 여부 확인
    private StarScrap existsStarScrap(Member member, Long targetId, ActType actType, TableType tableType) {
        Optional<StarScrap> starScrap = starScrapRepository.findByMemberAndActTypeAndTableTypeAndTargetId(member, actType, tableType, targetId);

        if (starScrap.isPresent()) {
            return starScrap.get();
        } return null;
    }

    // 존재하는 게시글인지 확인
    private Post existsPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        return post;
    }

    // TODO 존재하는 STUDY(+STUDYPOST)인지 확인



    /**
     * 해당 게시글 공감 혹은 스크랩 추가
     *
     * @param member 로그인 회원
     * @param targetId 해당 게시글 고유 id
     * @param actType STAR, SCRAP
     * @param tableType POST,  STUDY, STUDYPOST
     *
     * @return starScrap id
     */
    @Override
    public Long addStarScrap(Member member, Long targetId, ActType actType, TableType tableType) {
        // 관리자는 해당 기능 수행 불가능
        if (member.getRole() == Role.ADMIN) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }

        // 중복 요청 시
        if (existsStarScrap(member, targetId, actType, tableType) != null) {
            throw new CustomException(ErrorCode.DUPLICATE_STAR_SCRAP_REQUEST);
        }

        // 존재하는 게시글인지 확인
        if (tableType == TableType.POST) {
            Post post = existsPost(targetId);
            // 작성자 본인 공감 불가능
            if (post.getMember().getId() == member.getId()) {
                throw new CustomException(ErrorCode.PERMISSION_DENIED);
            }
        } else if (tableType == TableType.STUDY) {
            // TODO
        } else { // STUDYPOST
            // TODO
        }

        StarScrap starScrap = StarScrap.builder()
                .actType(actType)
                .tableType(tableType)
                .targetId(targetId)
                .member(member)
                .build();

        starScrap = starScrapRepository.save(starScrap);

        return starScrap.getId();
    }

    /**
     * 해당 게시글 공감 혹은 스크랩 삭제
     *
     * @param member 로그인 회원
     * @param targetId 해당 게시글 고유 id
     * @param actType STAR, SCRAP
     * @param tableType POST,  STUDY, STUDYPOST
     *
     * @return boolean 삭제 성공 시 true, 삭제 실패 시 false
     */
    @Override
    public boolean deleteStarScrap(Member member, Long targetId, ActType actType, TableType tableType) {
        // 관리자는 해당 기능 수행 불가능
        if (member.getRole() == Role.ADMIN) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }

        // 중복 요청 확인 (StarScrap 존재 여부 확인)
        StarScrap starScrap = existsStarScrap(member, targetId, actType, tableType);
        if (starScrap == null) {
            throw new CustomException(ErrorCode.INVALID_STAR_SCRAP_REQUEST);
        }

        starScrapRepository.delete(starScrap);

        return true;
    }

    // 총 공감 수 찾기
    @Override
    public int findStarCount(Long targetId) {
        return starScrapRepository.findAllByActTypeAndTableTypeAndTargetId(ActType.STAR, TableType.POST, targetId).size();
    }
}
