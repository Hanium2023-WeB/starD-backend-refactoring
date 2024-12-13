package com.web.stard.domain.post.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.web.stard.domain.post.domain.entity.Post;
import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.domain.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDto {

    @Getter
    @Builder
    public static class PostDto {
        @Schema(description = "게시글 아이디")
        private Long postId;

        @Schema(description = "게시글 제목")
        private String title;

        @Schema(description = "게시글 내용")
        private String content;

        @Schema(description = "커뮤니티 글 카테고리")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String category;

        @Schema(description = "조회수")
        private int hit;

        @Schema(description = "게시글 타입")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private PostType postType;

        @Schema(description = "작성자 여부")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean isAuthor;

        @Schema(description = "회원 닉네임")
        private String writer;

        @Schema(description = "회원 프로필 url")
        private String profileImg;

        @Schema(description = "게시글 생성 일시")
        private LocalDateTime createdAt;

        @Schema(description = "게시글 수정 일시")
        private LocalDateTime updatedAt;

        @Schema(description = "공감수")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer starCount;

        @Schema(description = "공감(스타) 존재 여부")
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
                    .createdAt(post.getCreatedAt())
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
