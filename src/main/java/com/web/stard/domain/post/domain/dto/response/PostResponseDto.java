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
        @Schema(description = "해당 게시글 고유 id")
        private Long postId;

        @Schema(description = "제목")
        private String title;

        @Schema(description = "내용")
        private String content;

        @Schema(description = "카테고리")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String category;

        @Schema(description = "조회수")
        private int hit;

        @Schema(description = "게시글 타입")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private PostType postType;

        @Schema(description = "회원 - 게시글 작성자 여부")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean isAuthor;

        @Schema(description = "작성자 닉네임")
        private String writer;

        @Schema(description = "작성자 프로필 이미지")
        private String profileImg;

        @Schema(description = "게시글 생성 일시")
        private LocalDateTime createdAt;

        @Schema(description = "게시글 수정 일시")
        private LocalDateTime updatedAt;

        @Schema(description = "공감 수")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer starCount;

        @Schema(description = "회원 - 공감 등록 여부")
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
                    .existsStar(existsStar)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class PostListDto {
        private List<PostDto> posts;

        @Schema(description = "현재 페이지")
        private int currentPage;

        @Schema(description = "전체 페이지 수")
        private int totalPages;

        @Schema(description = "마지막 페이지 여부")
        private boolean isLast;

        private long totalElements;

        public static PostListDto of(Page<Post> posts, List<PostDto> postDtos) {
            return PostListDto.builder()
                    .posts(postDtos)
                    .currentPage(posts.getNumber() + 1)
                    .totalPages(posts.getTotalPages())
                    .isLast(posts.isLast())
                    .totalElements(posts.getTotalElements())
                    .build();
        }
    }
}
