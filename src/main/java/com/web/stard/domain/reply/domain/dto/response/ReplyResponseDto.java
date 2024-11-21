package com.web.stard.domain.reply.domain.dto.response;

import com.web.stard.domain.reply.domain.entity.Reply;
import com.web.stard.domain.member.domain.entity.Member;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class ReplyResponseDto {

    @Getter
    @Builder
    public static class ReplyDto {
        private Long replyId;
        private String content;
        private String writer;
        private String profileImg;
        private LocalDateTime updatedAt;

        public static ReplyDto from(Reply reply, Member member) {
            return ReplyDto.builder()
                    .replyId(reply.getId())
                    .content(reply.getContent())
                    .writer(member.getNickname())
                    .profileImg(member.getProfile().getImgUrl())
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

        public static ReplyListDto of(Page<Reply> replies) {
            List<ReplyDto> replyDtos = replies.getContent().stream()
                    .map(reply -> ReplyDto.from(reply, reply.getMember()))
                    .toList();

            return ReplyListDto.builder()
                    .replies(replyDtos)
                    .currentPage(replies.getNumber() + 1)
                    .totalPages(replies.getTotalPages())
                    .isLast(replies.isLast())
                    .build();
        }
    }
}
