package com.web.stard.domain.chat.api;

import com.web.stard.domain.chat.domain.dto.response.ChatResponseDto;
import com.web.stard.domain.chat.service.ChatRoomService;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.global.domain.CurrentMember;
import com.web.stard.global.exception.ApiErrorCodeExamples;
import com.web.stard.global.exception.error.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
@Tag(name = "chats", description = "채팅 관련 API")
public class ChatRoomController {

    private final ChatRoomService chatService;

    @Operation(summary = "채팅방 생성")
    @ApiErrorCodeExamples({
            ErrorCode.CHAT_ROOM_ALREADY_EXISTS, ErrorCode.STUDY_NOT_FOUND
    })
    @PostMapping("/rooms/{studyId}")
    public ResponseEntity<ChatResponseDto.createChatRoomDto> createRoom(@PathVariable(name = "studyId") Long studyId) {
        return ResponseEntity.ok(chatService.createRoom(studyId));
    }

    @Operation(summary = "채팅 내역 조회")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND, ErrorCode.CHAT_ROOM_NOT_FOUND
    })
    @GetMapping("/history/{studyId}")
    public ResponseEntity<ChatResponseDto.ChatMessageListDto> getChatHistory(@PathVariable(name = "studyId") Long studyId,
                                                                             @CurrentMember Member member) {
        return ResponseEntity.ok(chatService.getChatHistory(studyId, member));
    }

}
