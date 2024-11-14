package com.web.stard.domain.board.global.application.impl;

import com.web.stard.domain.board.global.application.StarScrapService;
import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.domain.StarScrap;
import com.web.stard.domain.board.global.domain.enums.ActType;
import com.web.stard.domain.board.global.domain.enums.TableType;
import com.web.stard.domain.board.global.dto.response.PostResponseDto;
import com.web.stard.domain.board.global.repository.PostRepository;
import com.web.stard.domain.board.global.repository.StarScrapRepository;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.domain.enums.Role;
import com.web.stard.domain.study.domain.dto.StudyResponseDto;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.repository.StudyRepository;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StarScrapServiceImpl implements StarScrapService {

    private final StarScrapRepository starScrapRepository;
    private final PostRepository postRepository;
    private final StudyRepository studyRepository;


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

    // 존재하는 STUDY인지 확인
    private Study existsStudyRecruitPost(Long targetId) {
        Study study = studyRepository.findById(targetId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        return study;
    }

    // TODO 존재하는 STUDYPOST인지 확인



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
            existsStudyRecruitPost(targetId);
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

    /**
     * 해당 게시글의 모든 공감 혹은 스크랩 삭제 (글 삭제 시)
     *
     * @param targetId 해당 게시글 고유 id
     * @param actType STAR, SCRAP
     * @param tableType POST,  STUDY, STUDYPOST
     *
     */
    @Override
    public void deletePostStarScraps(Long targetId, ActType actType, TableType tableType) {
        List<StarScrap> starScraps = starScrapRepository.findAllByActTypeAndTableTypeAndTargetId(actType, tableType, targetId);
        starScrapRepository.deleteAll(starScraps);
    }

    // 총 공감 및 스크랩 수 찾기
    @Override
    public int findStarScrapCount(Long targetId, ActType actType, TableType tableType) {
        return starScrapRepository.findAllByActTypeAndTableTypeAndTargetId(actType, tableType, targetId).size();
    }

    /**
     * 사용자가 공감한 게시글 리스트 조회
     *
     * @param member 로그인 회원
     * @param page 조회할 페이지 번호
     *
     * @return PostListDto posts 게시글 리스트, currentPage 현재 페이지, totalPages 전체 페이지, isLast 마지막 페이지 여부
     */
    @Transactional(readOnly = true)
    @Override
    public PostResponseDto.PostListDto getMemberStarPostList(Member member, int page) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "id"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        Page<Post> posts = starScrapRepository.findPostsByMember(member, pageable);

        List<PostResponseDto.PostDto> postDtos = posts.getContent().stream()
                .map(post -> {
                    int starCount = findStarScrapCount(post.getId(), ActType.STAR, TableType.POST);
                    return PostResponseDto.PostDto.from(post, post.getMember(), starCount);
                })
                .toList();

        return PostResponseDto.PostListDto.of(posts, postDtos);
    }

    /**
     * 사용자가 스크랩한 게시글 리스트 조회
     *
     * @param member 로그인 회원
     * @param page 조회할 페이지 번호
     *
     * @return StudyRecruitListDto studyRecruitPosts, currentPage, totalPages, isLast
     *                              게시글 리스트         현재 페이지   전체 페이지  마지막 페이지 여부
     */
    @Transactional(readOnly = true)
    @Override
    public StudyResponseDto.StudyRecruitListDto getMemberScrapStudyList(Member member, int page) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "id"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        Page<Study> studyRecruitPosts = starScrapRepository.findStudyRecruitPostsByMember(member, pageable);

        List<StudyResponseDto.DetailInfo> studyPostDtos = studyRecruitPosts.getContent().stream()
                .map(post -> {
                    int scrapCount = findStarScrapCount(post.getId(), ActType.SCRAP, TableType.STUDY);
                    return StudyResponseDto.DetailInfo.toDto(post, post.getMember(), scrapCount);
                })
                .toList();

        return StudyResponseDto.StudyRecruitListDto.of(studyRecruitPosts, studyPostDtos);
    }
}
