package com.web.stard.domain.reply.domain.dto.response;

import com.web.stard.domain.reply.domain.entity.Reply;
import com.web.stard.domain.member.domain.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class ReplyResponseDto {

    @Getter
    @Builder
    public static class ReplyDto {
        @Schema(description = "댓글 아이디")
        private Long replyId;

        @Schema(description = "댓글 내용")
        private String content;

        @Schema(description = "작성자 여부")
        private boolean isAuthor;

        @Schema(description = "작성자 닉네임")
        private String writer;

        @Schema(description = "작성자 프로필 url")
        private String profileImg;

        @Schema(description = "댓글 작성 일시")
        private LocalDateTime createdAt;

        @Schema(description = "댓글 수정 일시")
        private LocalDateTime updatedAt;

        public static ReplyDto from(Reply reply, Member member, boolean isAuthor) {
            return ReplyDto.builder()
                    .replyId(reply.getId())
                    .content(reply.getContent())
                    .isAuthor(isAuthor)
                    .writer(member.getNickname())
                    .profileImg(member.getProfile().getImgUrl())
                    .createdAt(reply.getCreatedAt())
                    .updatedAt(reply.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ReplyListDto {
        private List<ReplyDto> replies;
        private int currentPage;    // 현재 페이지
        private int totalPages;     // 전체 페이지 수
        private boolean isLast;     // 마지막 페이지 여부

        public static ReplyListDto of(Page<Reply> replies, List<ReplyDto> replyDtos) {
            return ReplyListDto.builder()
                    .replies(replyDtos)
                    .currentPage(replies.getNumber() + 1)
                    .totalPages(replies.getTotalPages())
                    .isLast(replies.isLast())
                    .build();
        }
    }
}
