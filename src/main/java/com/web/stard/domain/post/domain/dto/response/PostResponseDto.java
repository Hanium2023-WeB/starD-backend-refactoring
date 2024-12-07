package com.web.stard.domain.post.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.web.stard.domain.post.domain.entity.Post;
import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.member.domain.entity.Member;
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
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String category;
        private int hit;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private PostType postType;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean isAuthor;
        private String writer;
        private String profileImg;
        private LocalDateTime updatedAt;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer starCount;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean existsStar;

        public static PostDto from(Post post, Member writer, Integer starCount, Boolean isAuthor, Boolean existsStar) {
            String writerName = (writer.getRole() == Role.ADMIN) ? "관리자" : writer.getNickname();
            String profileImage = (writer.getRole() == Role.ADMIN) ? null : writer.getProfile().getImgUrl();

            PostType type = post.getPostType();
            if (!(type == PostType.FAQ || type == PostType.QNA)) {
                type = null;
            }

            String category = null;
            if (post.getCategory() != null) {
                category = post.getCategory().getDescription();
            }

            return PostDto.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .category(category)
                    .hit(post.getHit())
                    .postType(type)
                    .isAuthor(isAuthor)
                    .writer(writerName)
                    .profileImg(profileImage)
                    .updatedAt(post.getUpdatedAt())
                    .starCount(starCount)
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

        public static PostListDto of(Page<Post> posts, List<PostDto> postDtos) {
            return PostListDto.builder()
                    .posts(postDtos)
                    .currentPage(posts.getNumber() + 1)
                    .totalPages(posts.getTotalPages())
                    .isLast(posts.isLast())
                    .build();
        }
    }
}
