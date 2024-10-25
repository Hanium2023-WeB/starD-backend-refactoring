package com.web.stard.domain.admin.dto.response;

import com.web.stard.domain.board.global.domain.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class NoticeResponseDto {

    @Getter
    @Builder
    public static class NoticeDto {
        private Long noticeId;
        private String title;
        private String content;
        private int hit;
        private String writer;
        private LocalDateTime createdAt;

        public static NoticeDto from(Post post) {
            return NoticeDto.builder()
                    .noticeId(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .hit(0)
                    .writer("관리자")
                    .createdAt(post.getCreatedAt())
                    .build();
        }
    }
}
