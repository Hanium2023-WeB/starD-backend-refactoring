package com.web.stard.domain.board.global.application.impl;

import com.web.stard.domain.board.global.application.PostService;
import com.web.stard.domain.board.global.dto.request.PostRequestDto;
import com.web.stard.domain.board.global.dto.response.PostResponseDto;
import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.domain.enums.PostType;
import com.web.stard.domain.board.global.repository.PostRepository;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.domain.enums.Role;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    // 관리자인지 확인
    private void isAdmin(Member member) {
        if (member.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
    }

    // 작성자인지 확인
    private void isPostAuthor(Member member, Post post) {
        if (!member.getId().equals(post.getMember().getId())) {
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }
    }

    // 게시글 찾기
    protected Post findPost(Long id, PostType type) {
        return postRepository.findByIdAndPostType(id, type)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    /**
     * 게시글 생성
     *
     * @param requestDto title 제목, content 내용
     * @return PostDto postId 게시글 id, title 제목, content 내용, hit 조회수, writer 작성자, profileImg 프로필 이미지, updatedAt 수정일시
     */
    @Override
    @Transactional
    public PostResponseDto.PostDto createPost(PostRequestDto.CreatePostDto requestDto, Member member, PostType postType) {
        if (postType == PostType.NOTICE || postType == PostType.FAQ) {
            isAdmin(member);
        }

        Post post = postRepository.save(requestDto.toEntity(member, postType));
        Member writer = memberRepository.findById(member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return PostResponseDto.PostDto.from(post, writer);
    }

    /**
     * 게시글 수정
     *
     * @param postId 수정할 게시글 id
     * @param requestDto title 제목, content 내용
     * @return PostDto postId 게시글 id, title 제목, content 내용, hit 조회수, writer 작성자, profileImg 프로필 이미지, updatedAt 수정일시
     */
    @Override
    @Transactional
    public PostResponseDto.PostDto updatePost(Long postId, PostRequestDto.CreatePostDto requestDto, Member member, PostType postType) {
        Post post = findPost(postId, postType);
        isPostAuthor(member, post);

        if (postType == PostType.NOTICE || postType == PostType.FAQ) {
            isAdmin(member);
        }

        post.updatePost(requestDto.getTitle(), requestDto.getContent());
        return PostResponseDto.PostDto.from(post, post.getMember());
    }

    /**
     * 게시글 삭제
     *
     * @param postId 삭제할 게시글 id
     * @return Long 삭제한 게시글 id
     */
    @Override
    @Transactional
    public Long deletePost(Long postId, Member member, PostType postType) {
        Post post = findPost(postId, postType);
        isPostAuthor(member, post);

        if (post.getPostType() == PostType.NOTICE || postType == PostType.FAQ) {
            isAdmin(member);
        }

        postRepository.delete(post);
        return postId;
    }

    /**
     * 게시글 목록 조회
     *
     * @param page 조회할 페이지 번호
     * @return PostListDto posts 게시글 리스트, currentPage 현재 페이지, totalPages 전체 페이지, isLast 마지막 페이지 여부
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponseDto.PostListDto getPostList(int page, PostType postType) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        Page<Post> posts = postRepository.findByPostType(postType, pageable);

        return PostResponseDto.PostListDto.of(posts);
    }

    /**
     * 게시글 상세 조회
     *
     * @param postId 조회할 게시글 id
     * @return PostDto postId 게시글 id, title 제목, content 내용, hit 조회수, writer 작성자, profileImg 프로필 이미지, updatedAt 수정일시
     */
    @Override
    @Transactional
    public PostResponseDto.PostDto getPostDetail(Long postId, Member member, PostType postType) {
        Post post = findPost(postId, postType);

        if (member == null || !member.getId().equals(post.getMember().getId())) {
            post.incrementHitCount();
        }

        return PostResponseDto.PostDto.from(post, post.getMember());
    }

    /**
     * 게시글 검색
     *
     * @param keyword 조회할 키워드
     * @param page 조회할 페이지 번호
     * @return PostListDto posts 게시글 리스트, currentPage 현재 페이지, totalPages 전체 페이지, isLast 마지막 페이지 여부
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponseDto.PostListDto searchPost(String keyword, int page, PostType postType) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        Page<Post> posts = postRepository.findByPostTypeAndTitleContainingOrContentContaining(
                                postType, keyword, keyword, pageable);

        return PostResponseDto.PostListDto.of(posts);
    }

    /**
     * faq, qna 순 목록 조회
     *
     * @param page 조회할 페이지 번호
     * @return PostListDto posts 게시글 리스트, currentPage 현재 페이지, totalPages 전체 페이지, isLast 마지막 페이지 여부
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponseDto.PostListDto getAllFaqsAndQnas(int page) {
        List<Post> posts = postRepository.findByPostTypeInOrderByPostTypeAscCreatedAtDesc(
                List.of(PostType.FAQ, PostType.QNA));

        Pageable pageable = PageRequest.of(page - 1, 10);
        return PostResponseDto.PostListDto.of(paginateList(posts, pageable));
    }

    /**
     * faq, qna 전체 검색
     *
     * @param keyword 조회할 키워드
     * @param page 조회할 페이지 번호
     * @return PostListDto posts 게시글 리스트, currentPage 현재 페이지, totalPages 전체 페이지, isLast 마지막 페이지 여부
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponseDto.PostListDto searchFaqsAndQnas(String keyword, int page) {
        List<PostType> postTypes = List.of(PostType.FAQ, PostType.QNA);

        List<Post> posts = postRepository.findByPostTypeInAndTitleOrContentContaining(
                                postTypes, keyword, PostType.FAQ, PostType.QNA);
        Pageable pageable = PageRequest.of(page - 1, 10);
        return PostResponseDto.PostListDto.of(paginateList(posts, pageable));
    }

    // 페이지 처리 메서드
    private <T> Page<T> paginateList(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();

        if (start >= list.size()) {
            throw new CustomException(ErrorCode.INVALID_PAGE);
        }

        int end = Math.min((start + pageable.getPageSize()), list.size());
        List<T> slicedResults = list.subList(start, end);
        return new PageImpl<>(slicedResults, pageable, list.size());
    }
}
