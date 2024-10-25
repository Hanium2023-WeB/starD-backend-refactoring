package com.web.stard.domain.admin.dto.request;

import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.domain.enums.Category;
import com.web.stard.domain.board.global.domain.enums.PostType;
import com.web.stard.domain.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

public class NoticeRequestDto {

    @Getter
    @Builder
    public static class CreateNoticeDto {

        @NotBlank(message = "제목은 필수입니다.")
        private String title;

        @NotBlank(message = "내용은 필수입니다.")
        private String content;

        public Post toEntity(Member member) {
            return Post.builder()
                    .title(title)
                    .content((content))
                    .postType(PostType.NOTICE)
                    .hit(0)
                    .member(member)
                    .build();
        }
    }
}
