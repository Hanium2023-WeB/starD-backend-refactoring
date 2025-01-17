package com.web.stard.domain.post.service.impl;

import com.web.stard.domain.reply.domain.entity.Reply;
import com.web.stard.domain.reply.repository.ReplyRepository;
import com.web.stard.domain.post.service.PostService;
import com.web.stard.domain.starScrap.service.StarScrapService;
import com.web.stard.domain.starScrap.domain.enums.ActType;
import com.web.stard.domain.post.domain.enums.Category;
import com.web.stard.domain.starScrap.domain.enums.TableType;
import com.web.stard.domain.post.domain.dto.request.PostRequestDto;
import com.web.stard.domain.post.domain.dto.response.PostResponseDto;
import com.web.stard.domain.post.domain.entity.Post;
import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.post.repository.PostRepository;
import com.web.stard.domain.member.domain.entity.Member;
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
    private final StarScrapService starScrapService;
    private final ReplyRepository replyRepository;

    // 관리자인지 확인
    private void isAdmin(Member member) {
        if (member.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
    }

    // 작성자인지 확인
    private Boolean isPostAuthor(Member member, Post post) {
        if (!member.getId().equals(post.getMember().getId())) {
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }
        return true;
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

        return PostResponseDto.PostDto.from(post, writer, 0, true, false);
    }

    /**
     * 커뮤니티 게시글 생성
     *
     * @param requestDto title, content, category
     *                   제목     내용      카테고리
     * @return CommPostDto  commPostId, title, content, category, hit, writer, profileImg, updatedAt
     * 게시글 id    제목      내용     카테고리  조회수 작성자 프로필 이미지   수정일시
     */
    @Override
    @Transactional
    public PostResponseDto.PostDto createCommPost(Member member, PostRequestDto.CreateCommPostDto requestDto) {
        Post post = postRepository.save(requestDto.toEntity(member));
        Member writer = memberRepository.findById(member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return PostResponseDto.PostDto.from(post, writer, 0, true, false);
    }

    /**
     * 게시글 수정
     *
     * @param postId     수정할 게시글 id
     * @param requestDto title 제목, content 내용
     * @return PostDto postId 게시글 id, title 제목, content 내용, hit 조회수, writer 작성자, profileImg 프로필 이미지, updatedAt 수정일시
     */
    @Override
    @Transactional
    public PostResponseDto.PostDto updatePost(Long postId, PostRequestDto.CreatePostDto requestDto, Member member, PostType postType) {
        Post post = findPost(postId, postType);
        Boolean isAuthor = isPostAuthor(member, post);

        if (postType == PostType.NOTICE || postType == PostType.FAQ) {
            isAdmin(member);
        }

        post.updatePost(requestDto.getTitle(), requestDto.getContent());
        int starCount = starScrapService.findStarScrapCount(post.getId(), ActType.STAR, TableType.POST);

        return PostResponseDto.PostDto.from(post, post.getMember(), starCount, isAuthor, false);
    }

    /**
     * 커뮤니티 게시글 수정
     *
     * @param commPostId 수정할 게시글의 id
     * @param requestDto title, content, category
     *                   제목     내용      카테고리
     * @return CommPostDto  commPostId, title, content, category, hit, writer, profileImg, updatedAt
     * 게시글 id    제목      내용     카테고리  조회수 작성자  프로필 이미지   수정일시
     */
    @Transactional
    @Override
    public PostResponseDto.PostDto updateCommPost(Member member, Long commPostId, PostRequestDto.CreateCommPostDto requestDto) {
        Post post = findPost(commPostId, PostType.COMM);
        Boolean isAuthor = isPostAuthor(member, post);

        post.updateComm(requestDto.getTitle(), requestDto.getContent(), Category.find(requestDto.getCategory()));
        int starCount = starScrapService.findStarScrapCount(post.getId(), ActType.STAR, TableType.POST);

        return PostResponseDto.PostDto.from(post, post.getMember(), starCount, isAuthor, false);
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

        if (post.getPostType() == PostType.NOTICE || postType == PostType.FAQ) {
            isAdmin(member);
        } else {
            isPostAuthor(member, post);
        }

        // 공감 삭제
        starScrapService.deletePostStarScraps(postId, ActType.STAR, TableType.POST);

        // 댓글 삭제
        if (post.getPostType() == PostType.COMM || post.getPostType() == PostType.QNA) {
            List<Reply> replies = replyRepository.findAllByTargetIdAndPostType(postId, postType);
            replyRepository.deleteAll(replies);
        }

        postRepository.delete(post);
        return postId;
    }

    // 목록 조회 - 공감 수 추가 메서드
    private List<PostResponseDto.PostDto> findAllStarCount(Page<Post> posts, Member member) {
        List<PostResponseDto.PostDto> postDtos = posts.getContent().stream()
                .map(post -> {
                    int starCount = starScrapService.findStarScrapCount(post.getId(), ActType.STAR, TableType.POST);
                    Boolean existsStar = null;
                    if (member != null) {
                        existsStar = (starScrapService.existsStarScrap(member, post.getId(), ActType.STAR, TableType.POST) != null);
                    }
                    return PostResponseDto.PostDto.from(post, post.getMember(), starCount, null, existsStar);
                })
                .toList();

        return postDtos;
    }

    /**
     * 게시글 목록 조회
     *
     * @param page 조회할 페이지 번호
     * @return PostListDto posts 게시글 리스트, currentPage 현재 페이지, totalPages 전체 페이지, isLast 마지막 페이지 여부
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponseDto.PostListDto getPostList(int page, PostType postType, Member member) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        Page<Post> posts = postRepository.findByPostType(postType, pageable);

        List<PostResponseDto.PostDto> postDtos = findAllStarCount(posts, member);

        return PostResponseDto.PostListDto.of(posts, postDtos);
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
        Boolean isAuthor = (member != null && post.getMember().getId().equals(member.getId()));

        if (member == null || !isAuthor) {
            post.incrementHitCount();
        }

        int starCount = starScrapService.findStarScrapCount(post.getId(), ActType.STAR, TableType.POST);
        Boolean existsStar = (starScrapService.existsStarScrap(member, post.getId(), ActType.STAR, TableType.POST) != null);

        return PostResponseDto.PostDto.from(post, post.getMember(), starCount, isAuthor, existsStar);
    }

    /**
     * 게시글 검색
     *
     * @param keyword 조회할 키워드
     * @param page    조회할 페이지 번호
     * @return PostListDto posts 게시글 리스트, currentPage 현재 페이지, totalPages 전체 페이지, isLast 마지막 페이지 여부
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponseDto.PostListDto searchPost(String keyword, int page, PostType postType, Member member) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        Page<Post> posts = postRepository.findByPostTypeAndKeyword(postType, keyword, pageable);
        List<PostResponseDto.PostDto> postDtos = findAllStarCount(posts, member);
        return PostResponseDto.PostListDto.of(posts, postDtos);
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
        Page<Post> paginatePostList = paginateList(posts, pageable);

        List<PostResponseDto.PostDto> postDtos = paginatePostList.getContent().stream()
                .map(post -> {
                    return PostResponseDto.PostDto.from(post, post.getMember(), null, null, null);
                }).toList();

        return PostResponseDto.PostListDto.of(paginatePostList, postDtos);
    }

    /**
     * faq, qna 전체 검색
     *
     * @param keyword 조회할 키워드
     * @param page    조회할 페이지 번호
     * @return PostListDto posts 게시글 리스트, currentPage 현재 페이지, totalPages 전체 페이지, isLast 마지막 페이지 여부
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponseDto.PostListDto searchFaqsAndQnas(String keyword, int page) {
        List<PostType> postTypes = List.of(PostType.FAQ, PostType.QNA);

        List<Post> posts = postRepository.findByPostTypeInAndTitleOrContentContaining(
                postTypes, keyword, PostType.FAQ, PostType.QNA);
        Pageable pageable = PageRequest.of(page - 1, 10);

        Page<Post> paginatePostList = paginateList(posts, pageable);

        List<PostResponseDto.PostDto> postDtos = paginatePostList.getContent().stream()
                .map(post -> {
                    return PostResponseDto.PostDto.from(post, post.getMember(), null, null, null);
                }).toList();

        return PostResponseDto.PostListDto.of(paginatePostList, postDtos);
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

    /**
     * 커뮤니티 게시글 리스트 조회 - 카테고리 선택
     *
     * @param category 조회할 카테고리
     * @param page     조회할 페이지 번호
     * @return CommPostListDto  commPostList, currentPage, totalPages, isLast
     * 커뮤니티 게시글 리스트  현재 페이지   전체 페이지   마지막 페이지 여부
     */
    @Transactional(readOnly = true)
    @Override
    public PostResponseDto.PostListDto getCommPostListByCategory(String category, int page, Member member) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        Page<Post> posts = postRepository.findByPostTypeAndCategory(PostType.COMM, Category.find(category), pageable);

        List<PostResponseDto.PostDto> postDtos = findAllStarCount(posts, member);

        return PostResponseDto.PostListDto.of(posts, postDtos);
    }

    /**
     * 커뮤니티 게시글 - 키워드 + 카테고리 검색 조회
     *
     * @param keyword  검색할 키워드
     * @param category 검색할 카테고리
     * @param page     조회할 페이지 번호
     * @return CommPostListDto  commPostList, currentPage, totalPages, isLast
     * 커뮤니티 게시글 리스트  현재 페이지   전체 페이지   마지막 페이지 여부
     */
    @Transactional(readOnly = true)
    @Override
    public PostResponseDto.PostListDto searchCommPostWithCategory(String keyword, String category, int page, Member member) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        Page<Post> posts = postRepository.searchCommPostWithCategory(PostType.COMM, keyword, Category.find(category), pageable);

        List<PostResponseDto.PostDto> postDtos = findAllStarCount(posts, member);

        return PostResponseDto.PostListDto.of(posts, postDtos);
    }

    /**
     * 사용자가 작성한 커뮤니티 게시글 조회
     *
     * @param member 사용자
     * @param page   조회할 페이지 번호
     * @return CommPostListDto  commPostList, currentPage, totalPages, isLast
     * 커뮤니티 게시글 리스트  현재 페이지   전체 페이지   마지막 페이지 여부
     */
    @Transactional(readOnly = true)
    @Override
    public PostResponseDto.PostListDto getCommPostListByMember(Member member, int page) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        Page<Post> posts = postRepository.findByMemberAndPostType(member, PostType.COMM, pageable);

        List<PostResponseDto.PostDto> postDtos = findAllStarCount(posts, member);

        return PostResponseDto.PostListDto.of(posts, postDtos);
    }
}
