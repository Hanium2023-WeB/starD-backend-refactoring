package com.web.stard.domain.reply.service;

import com.web.stard.domain.reply.domain.dto.request.ReplyRequestDto;
import com.web.stard.domain.reply.domain.dto.response.ReplyResponseDto;
import com.web.stard.domain.member.domain.entity.Member;

public interface ReplyService {
    ReplyResponseDto.ReplyDto createReply(Long targetId, ReplyRequestDto.CreateReplyDto requestDto, Member member);

    ReplyResponseDto.ReplyDto updateReply(Long replyId, ReplyRequestDto.UpdateReplyDto requestDto, Member member);

    Long deleteReply(Long replyId, Member member);

    ReplyResponseDto.ReplyListDto getReplyList(Long targetId, String type, int page, Member member);
}
