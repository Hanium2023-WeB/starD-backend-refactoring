package com.web.stard.domain.admin.application.impl;

import com.web.stard.domain.admin.application.NoticeService;
import com.web.stard.domain.admin.dto.request.NoticeRequestDto;
import com.web.stard.domain.admin.dto.response.NoticeResponseDto;
import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.domain.enums.PostType;
import com.web.stard.domain.board.global.repository.PostRepository;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.domain.enums.Role;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.global.error.CustomException;
import com.web.stard.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

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

    /**
     * 공지사항 생성
     *
     * @param requestDto     title, content
     *                       제목     내용
     * @return NoticeDto     noticeId, title, content, hit, writer, updatedAt
     *                       공지 id    제목    내용     조회수 작성자     수정일시
     */
    @Override
    public NoticeResponseDto.NoticeDto createNotice(NoticeRequestDto.CreateNoticeDto requestDto) {
        // TODO - 로그인한 멤버 정보로 변경
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        isAdmin(member);
        Post notice = postRepository.save(requestDto.toEntity(member));

        return NoticeResponseDto.NoticeDto.from(notice);
    }

    /**
     * 공지사항 수정
     *
     * @param noticeId      수정할 공지사항의 id
     * @param requestDto    title, content
     *                      제목     내용
     * @return NoticeDto    noticeId, title, content, hit, writer, updatedAt
     *                      공지 id    제목    내용     조회수 작성자     수정일시
     */
    @Override
    public NoticeResponseDto.NoticeDto updateNotice(Long noticeId, NoticeRequestDto.CreateNoticeDto requestDto) {
        // TODO - 로그인한 멤버 정보로 변경
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post notice = postRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        isAdmin(member);
        isPostAuthor(member,notice);

        // 공지 수정
        notice.updatePost(requestDto.getTitle(), requestDto.getContent());
        Post updatedNotice = postRepository.save(notice);

        return NoticeResponseDto.NoticeDto.from(updatedNotice);
    }

    /**
     * 공지사항 삭제
     *
     * @param noticeId      삭제할 공지사항의 id
     * @return Long         삭제한 공지사항의 id
     */
    @Override
    public Long deleteNotice(Long noticeId) {
        // TODO - 로그인한 멤버 정보로 변경
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post notice = postRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        isAdmin(member);
        isPostAuthor(member,notice);

        // 공지 삭제
        postRepository.delete(notice);
        return noticeId;
    }

    /**
     * 공지사항 목록 조회
     *
     * @param page              조회할 페이지 번호
     * @return NoticeListDto    notices, currentPage, totalPages, isLast
     *                          공지 리스트  현재 페이지   전체 페이지   마지막 페이지 여부
     */
    @Override
    public NoticeResponseDto.NoticeListDto getNoticeList(int page) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        Page<Post> notices = postRepository.findByPostType(PostType.NOTICE, pageable);

        return NoticeResponseDto.NoticeListDto.of(notices);
    }

    /**
     * 공지사항 상세 조회
     *
     * @param noticeId      조회할 공지사항의 id
     * @return NoticeDto    noticeId, title, content, hit, writer, updatedAt
     *                      공지 id    제목    내용     조회수 작성자     수정일시
     */
    @Override
    public NoticeResponseDto.NoticeDto getNoticeDetail(Long noticeId) {
        Post notice = postRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        return NoticeResponseDto.NoticeDto.from(notice);
    }

    /**
     * 공지사항 검색
     *
     * @param keyword           조회할 키워드
     * @param page              조회할 페이지 번호
     * @return NoticeListDto    notices, currentPage, totalPages, isLast
     *                          공지 리스트  현재 페이지   전체 페이지   마지막 페이지 여부
     */
    @Override
    public NoticeResponseDto.NoticeListDto searchNotice(String keyword, int page) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);

        Page<Post> notices = postRepository.findByPostTypeAndTitleContainingOrContentContaining(
                                PostType.NOTICE, keyword, keyword, pageable);

        return NoticeResponseDto.NoticeListDto.of(notices);
    }
}
