package com.web.stard.domain.chat.service;

import com.web.stard.domain.chat.domain.dto.response.ChatResponseDto;

public interface ChatRoomService {
    ChatResponseDto.createChatRoomDto createRoom(Long studyId);

    ChatResponseDto.ChatMessageListDto getChatHistory(Long studyId);
}
