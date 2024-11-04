package com.web.stard.domain.board.global.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.domain.enums.PostType;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.domain.enums.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDto {

    @Getter
    @Builder
    public static class PostDto {
        private Long postId;
        private String title;
        private String content;
        private int hit;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private PostType postType;
        private String writer;
        private String profileImg;
        private LocalDateTime updatedAt;

        public static PostDto from(Post post, Member writer) {
            String writerName = (writer.getRole() == Role.ADMIN) ? "관리자" : writer.getNickname();
            String profileImage = (writer.getRole() == Role.ADMIN) ? null : writer.getProfile().getImgUrl();

            PostType type = post.getPostType();
            if (!(type == PostType.FAQ || type == PostType.QNA)) {
                type = null;
            }

            return PostDto.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .hit(post.getHit())
                    .postType(type)
                    .writer(writerName)
                    .profileImg(profileImage)
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class PostListDto {
        private List<PostDto> posts;
        private int currentPage;    // 현재 페이지
        private int totalPages;     // 전체 페이지 수
        private boolean isLast;     // 마지막 페이지 여부

        public static PostListDto of(Page<Post> posts) {
            List<PostDto> postDtos = posts.getContent().stream()
                    .map(post -> PostDto.from(post, post.getMember()))
                    .toList();

            return PostListDto.builder()
                    .posts(postDtos)
                    .currentPage(posts.getNumber() + 1)
                    .totalPages(posts.getTotalPages())
                    .isLast(posts.isLast())
                    .build();
        }
    }
}
