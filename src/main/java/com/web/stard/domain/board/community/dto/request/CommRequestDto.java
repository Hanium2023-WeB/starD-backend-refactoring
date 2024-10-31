package com.web.stard.domain.board.community.dto.request;

import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.domain.enums.Category;
import com.web.stard.domain.board.global.domain.enums.PostType;
import com.web.stard.domain.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

public class CommRequestDto {

    @Getter
    @Builder
    public static class CreateCommPostDto {
        private Long memberId; // todo: 로그인 한 사용자로 변경

        @Size(max = 100, message = "제목은 최대 100자 이내여야 합니다.")
        @NotBlank(message = "제목은 필수입니다.")
        private String title;

        @Size(max = 1000, message = "내용은 최대 1000자 이내여야 합니다.")
        @NotBlank(message = "내용은 필수입니다.")
        private String content;

        @NotBlank
        private String category;

        public Post toEntity(Member member) {
            return Post.builder()
                    .title(title)
                    .content(content)
                    .category(Category.find(category))
                    .postType(PostType.COMM)
                    .hit(0)
                    .member(member)
                    .build();
        }
    }
}
