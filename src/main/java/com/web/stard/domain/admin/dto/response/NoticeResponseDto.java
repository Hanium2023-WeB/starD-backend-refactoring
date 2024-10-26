package com.web.stard.domain.admin.dto.response;

import com.web.stard.domain.board.global.domain.Post;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class NoticeResponseDto {

    @Getter
    @Builder
    public static class NoticeDto {
        private Long noticeId;
        private String title;
        private String content;
        private int hit;
        private String writer;
        private LocalDateTime updatedAt;

        public static NoticeDto from(Post post) {
            return NoticeDto.builder()
                    .noticeId(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .hit(0)
                    .writer("관리자")
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class NoticeListDto {
        private List<NoticeDto> notices;
        private int currentPage;    // 현재 페이지
        private int totalPages;     // 전체 페이지 수
        private boolean isLast;     // 마지막 페이지 여부

        public static NoticeListDto of(Page<Post> notices) {
            List<NoticeDto> noticeDtos = notices.getContent().stream()
                    .map(NoticeDto::from)
                    .toList();

            return NoticeListDto.builder()
                    .notices(noticeDtos)
                    .currentPage(notices.getNumber() + 1)
                    .totalPages(notices.getTotalPages())
                    .isLast(notices.isLast())
                    .build();
        }
    }
}
