package com.web.stard.domain.board.community.dto.response;

import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.domain.enums.Category;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class CommResponseDto {

    @Getter
    @Builder
    public static class CommPostDto {
        private Long commPostId;
        private String title;
        private String content;
        private Category category;
        private int hit;
        private String writer;
        private LocalDateTime updatedAt;

        public static CommPostDto from(Post post) {
            return CommPostDto.builder()
                    .commPostId(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .category(post.getCategory())
                    .hit(post.getHit())
                    .writer(post.getMember().getNickname())
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }
    }
}
