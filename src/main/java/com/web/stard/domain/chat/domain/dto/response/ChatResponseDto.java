package com.web.stard.domain.chat.domain.dto.response;

import com.web.stard.domain.chat.domain.entity.ChatMessage;
import com.web.stard.domain.chat.domain.entity.ChatRoom;
import com.web.stard.domain.chat.domain.enums.MessageType;
import com.web.stard.domain.member.domain.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ChatResponseDto {

    @Getter
    @Builder
    public static class createChatRoomDto {
        @Schema(description = "채팅방 id")
        private Long chatRoomId;

        @Schema(description = "스터디 id")
        private Long studyId;

        public static ChatResponseDto.createChatRoomDto from(ChatRoom chatRoom) {
            return createChatRoomDto.builder()
                    .chatRoomId(chatRoom.getId())
                    .studyId(chatRoom.getStudy().getId())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageDto {
        @Schema(description = "메시지 id")
        private Long messageId;

        @Schema(description = "메시지 내용")
        private String message;

        @Schema(description = "메시지 타입")
        private MessageType messageType;

        @Schema(description = "작성자 닉네임")
        private String nickname;

        @Schema(description = "작성자 여부")
        private boolean isAuthor;
        
        @Schema(description = "작성 일시")
        private LocalDateTime createdAt;

        public static ChatMessageDto of(ChatMessage chatMessage, Member member) {
            return ChatMessageDto.builder()
                    .messageId(chatMessage.getId())
                    .message(chatMessage.getMessage())
                    .messageType(chatMessage.getMessageType())
                    .nickname(chatMessage.getStudyMember().getMember().getNickname())
                    .isAuthor(chatMessage.getStudyMember().getMember().getNickname().equals(member.getNickname()))
                    .createdAt(chatMessage.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ChatMessageListDto {
        @Schema(description = "채팅방 id")
        private Long chatRoomId;

        @Schema(description = "채팅 내역 리스트")
        private List<ChatMessageDto> chatMessages;

        public static ChatMessageListDto of(ChatRoom chatRoom, List<ChatMessage> chatMessages, Member member) {
            return ChatMessageListDto.builder()
                    .chatRoomId(chatRoom.getId())
                    .chatMessages(
                            chatMessages.stream()
                                    .map(chatMessage -> ChatMessageDto.of(chatMessage, member))
                                    .collect(Collectors.toList())
                    )
                    .build();
        }
    }

}
