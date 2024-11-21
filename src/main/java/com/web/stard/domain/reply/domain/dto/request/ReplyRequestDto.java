package com.web.stard.domain.reply.domain.dto.request;

import com.web.stard.domain.reply.domain.entity.Reply;
import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.member.domain.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReplyRequestDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateReplyDto {
        @Size(max = 1000, message = "내용은 최대 1000자 이내여야 합니다.")
        @NotBlank(message = "내용은 필수입니다.")
        private String content;

        @NotBlank(message = "타입은 필수입니다.")
        private String type;

        public Reply toEntity(Member member, Long targetId, PostType postType) {
            return Reply.builder()
                    .content(content)
                    .targetId(targetId)
                    .postType(postType)
                    .member(member)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReplyDto {
        @Size(max = 1000, message = "내용은 최대 1000자 이내여야 합니다.")
        @NotBlank(message = "내용은 필수입니다.")
        private String content;
    }
}
