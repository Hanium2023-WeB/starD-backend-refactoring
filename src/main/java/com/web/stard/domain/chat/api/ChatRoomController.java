package com.web.stard.domain.chat.api;

import com.web.stard.domain.chat.domain.dto.response.ChatResponseDto;
import com.web.stard.domain.chat.service.ChatRoomService;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.global.domain.CurrentMember;
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
    @PostMapping("/rooms/{studyId}")
    public ResponseEntity<ChatResponseDto.createChatRoomDto> createRoom(@PathVariable(name = "studyId") Long studyId) {
        return ResponseEntity.ok(chatService.createRoom(studyId));
    }

    @Operation(summary = "채팅 내역 조회")
    @GetMapping("/history/{studyId}")
    public ResponseEntity<ChatResponseDto.ChatMessageListDto> getChatHistory(@PathVariable(name = "studyId") Long studyId,
                                                                             @CurrentMember Member member) {
        return ResponseEntity.ok(chatService.getChatHistory(studyId, member));
    }

}
