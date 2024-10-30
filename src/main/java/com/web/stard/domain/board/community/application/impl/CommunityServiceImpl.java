package com.web.stard.domain.board.community.application.impl;

import com.web.stard.domain.board.community.application.CommunityService;
import com.web.stard.domain.board.community.dto.request.CommRequestDto;
import com.web.stard.domain.board.community.dto.response.CommResponseDto;
import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.domain.enums.Category;
import com.web.stard.domain.board.global.domain.enums.PostType;
import com.web.stard.domain.board.global.repository.PostRepository;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    // 작성자인지 확인
    private boolean isPostAuthor(Member member, Post post) {
        return member.getId().equals(post.getMember().getId());
    }

    /**
     * 커뮤니티 게시글 생성
     *
     * @param requestDto     title, content, category
     *                       제목     내용      카테고리
     * @return CommPostDto  commPostId, title, content, category, hit, writer, updatedAt
     *                       게시글 id    제목      내용     카테고리  조회수 작성자     수정일시
     */
    @Override
    public CommResponseDto.CommPostDto createCommPost(CommRequestDto.CreateCommPostDto requestDto) {
        // 회원 정보 반환
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.save(requestDto.toEntity(member));

        return CommResponseDto.CommPostDto.from(post);
    }

    /**
     * 커뮤니티 게시글 수정
     *
     * @param commPostId      수정할 게시글의 id
     * @param requestDto     title, content, category
     *                       제목     내용      카테고리
     * @return CommPostDto  commPostId, title, content, category, hit, writer, updatedAt
     *                       게시글 id    제목      내용     카테고리  조회수 작성자     수정일시
     */
    @Transactional
    @Override
    public CommResponseDto.CommPostDto updateCommPost(Long commPostId, CommRequestDto.CreateCommPostDto requestDto) {
        // 회원 정보 반환
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(commPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if(!isPostAuthor(member, post)) {
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }

        post.updateComm(requestDto.getTitle(), requestDto.getContent(), Category.find(requestDto.getCategory()));
        Post updatedPost = postRepository.save(post);

        return CommResponseDto.CommPostDto.from(updatedPost);
    }

    /**
     * 커뮤니티 게시글 삭제
     *
     * @param commPostId      수정할 게시글의 id
     * @return 없음
     */
    @Transactional
    @Override
    public ResponseEntity<String> deleteCommPost(Long commPostId, Long memberId) {
        // 회원 정보 반환 TODO: 로그인한 회원
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(commPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if(!isPostAuthor(member, post)) {
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }

        postRepository.delete(post);

        return ResponseEntity.status(HttpStatus.OK).body("게시글을 삭제하였습니다.");
    }

    /**
     * 커뮤니티 게시글 상세조회
     *
     * @param commPostId      조회할 게시글의 id
     * @return CommPostDto  commPostId, title, content, category, hit, writer, updatedAt
     *                       게시글 id    제목     내용     카테고리  조회수 작성자     수정일시
     */
    @Transactional
    @Override
    public CommResponseDto.CommPostDto getCommPostDetail(Long commPostId, Long memberId) {
        // 회원 정보 반환 TODO: 로그인한 회원
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(commPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 작성자가 아니라면 조회수 +1
        if (!isPostAuthor(member, post)) {
            post.incrementHitCount();
            post = postRepository.save(post);
        }

        return CommResponseDto.CommPostDto.from(post);
    }

    /**
     * 커뮤니티 게시글 리스트 조회 (카테고리 - 전체)
     *
     * @param page              조회할 페이지 번호
     * @return CommPostListDto  commPostList, currentPage, totalPages, isLast
     *                     커뮤니티 게시글 리스트  현재 페이지   전체 페이지   마지막 페이지 여부
     */
    @Transactional(readOnly = true)
    @Override
    public CommResponseDto.CommPostListDto getCommPostList(int page) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        Page<Post> posts = postRepository.findByPostType(PostType.COMM, pageable);

        return CommResponseDto.CommPostListDto.of(posts);
    }

    /**
     * 커뮤니티 게시글 리스트 조회 - 카테고리 선택
     *
     * @param category          조회할 카테고리
     * @param page              조회할 페이지 번호
     * @return CommPostListDto  commPostList, currentPage, totalPages, isLast
     *                     커뮤니티 게시글 리스트  현재 페이지   전체 페이지   마지막 페이지 여부
     */
    @Transactional(readOnly = true)
    @Override
    public CommResponseDto.CommPostListDto getCommPostListByCategory(String category, int page) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        Page<Post> posts = postRepository.findByPostTypeAndCategory(PostType.COMM, Category.find(category), pageable);

        return CommResponseDto.CommPostListDto.of(posts);
    }

    /**
     * 커뮤니티 게시글 - 키워드 검색 조회 (카테고리 - 전체)
     *
     * @param keyword           검색할 키워드
     * @param page              조회할 페이지 번호
     * @return CommPostListDto  commPostList, currentPage, totalPages, isLast
     *                     커뮤니티 게시글 리스트  현재 페이지   전체 페이지   마지막 페이지 여부
     */
    @Transactional(readOnly = true)
    @Override
    public CommResponseDto.CommPostListDto searchCommPost(String keyword, int page) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        Page<Post> posts = postRepository.findByPostTypeAndTitleContainingOrContentContaining(
                            PostType.COMM, keyword, keyword, pageable);

        return CommResponseDto.CommPostListDto.of(posts);
    }

    /**
     * 커뮤니티 게시글 - 키워드 + 카테고리 검색 조회
     *
     * @param keyword           검색할 키워드
     * @param category          검색할 카테고리
     * @param page              조회할 페이지 번호
     * @return CommPostListDto  commPostList, currentPage, totalPages, isLast
     *                     커뮤니티 게시글 리스트  현재 페이지   전체 페이지   마지막 페이지 여부
     */
    @Transactional(readOnly = true)
    @Override
    public CommResponseDto.CommPostListDto searchCommPostWithCategory(String keyword, String category, int page) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        Page<Post> posts = postRepository.searchCommPostWithCategory(PostType.COMM, keyword, Category.find(category), pageable);

        return CommResponseDto.CommPostListDto.of(posts);
    }
}
