package com.web.stard.domain.chat.api;

import com.web.stard.domain.chat.domain.dto.response.ChatResponseDto;
import com.web.stard.domain.chat.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatRoomController {

    private final ChatRoomService chatService;

    @Operation(summary = "채팅방 생성")
    @PostMapping("/rooms/{studyId}")
    public ResponseEntity<ChatResponseDto.createChatRoomDto> createRoom(@PathVariable(name = "studyId") Long studyId) {
        return ResponseEntity.ok(chatService.createRoom(studyId));
    }

    @Operation(summary = "채팅 내역 조회")
    @GetMapping("/history/{studyId}")
    public ResponseEntity<ChatResponseDto.ChatMessageListDto> getChatHistory(@PathVariable(name = "studyId") Long studyId) {
        return ResponseEntity.ok(chatService.getChatHistory(studyId));
    }

}
