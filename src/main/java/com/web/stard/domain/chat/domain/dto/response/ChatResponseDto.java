package com.web.stard.domain.chat.domain.dto.response;

import com.web.stard.domain.chat.domain.entity.ChatMessage;
import com.web.stard.domain.chat.domain.entity.ChatRoom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

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
    public static class ChatMessageDto {
        @Schema(description = "메시지 id")
        private Long messageId;

        @Schema(description = "메시지 내용")
        private String message;

        @Schema(description = "메시지 이미지 url")
        private String imgUrl;

        @Schema(description = "작성자 닉네임")
        private String nickname;

        @Schema(description = "작성자 프로필 url")
        private String profileImg;

        @Schema(description = "작성 일시")
        private LocalDateTime createdAt;

        public static ChatMessageDto of(ChatMessage chatMessage) {
            return ChatMessageDto.builder()
                    .messageId(chatMessage.getId())
                    .message(chatMessage.getMessage())
                    .imgUrl(chatMessage.getImgUrl())
                    .nickname(chatMessage.getStudyMember().getMember().getNickname())
                    .profileImg(chatMessage.getStudyMember().getMember().getProfile().getImgUrl())
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

        public static ChatMessageListDto of(ChatRoom chatRoom, List<ChatMessage> chatMessages) {
            return ChatMessageListDto.builder()
                    .chatRoomId(chatRoom.getId())
                    .chatMessages(
                            chatMessages.stream()
                                    .map(ChatMessageDto::of)
                                    .collect(Collectors.toList())
                    )
                    .build();
        }
    }

}
