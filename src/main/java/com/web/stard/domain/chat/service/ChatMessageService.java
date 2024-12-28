package com.web.stard.domain.chat.service;

import com.web.stard.domain.chat.domain.dto.response.ChatResponseDto;
import com.web.stard.domain.member.domain.entity.Member;
import org.springframework.web.multipart.MultipartFile;

public interface ChatMessageService {
    ChatResponseDto.ChatMessageDto saveChatMessage(String message, Member member);
}
