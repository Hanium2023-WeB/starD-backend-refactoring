package com.web.stard.domain.board.community.dto.response;

import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.domain.enums.Category;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

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
        private LocalDateTime createAt;
        private LocalDateTime updatedAt;

        public static CommPostDto from(Post post) {
            return CommPostDto.builder()
                    .commPostId(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .category(post.getCategory())
                    .hit(post.getHit())
                    .writer(post.getMember().getNickname())
                    .createAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class CommPostListDto {
        private List<CommPostDto> commPostList;
        private int currentPage;    // 현재 페이지
        private int totalPages;     // 전체 페이지 수
        private boolean isLast;     // 마지막 페이지 여부

        public static CommPostListDto of(Page<Post> posts) {
            return CommPostListDto.builder()
                    .commPostList(posts.getContent().stream().map(CommPostDto::from).toList())
                    .currentPage(posts.getNumber() + 1)
                    .totalPages(posts.getTotalPages())
                    .isLast(posts.isLast())
                    .build();
        }
    }
}
