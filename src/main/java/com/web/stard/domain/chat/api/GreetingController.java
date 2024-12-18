package com.web.stard.domain.chat.api;

import com.web.stard.domain.chat.domain.dto.response.ChatResponseDto;
import com.web.stard.domain.chat.service.ChatMessageService;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.global.domain.CurrentMember;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class GreetingController {

    private final ChatMessageService chatMessageService;

    // 입장
    @MessageMapping("/enter/{studyId}")
    @SendTo("/topic/greetings/{studyId}")
    public ChatResponseDto.ChatMessageDto enter(@CurrentMember Member member) {
        String message = HtmlUtils.htmlEscape(member.getNickname() + "님이 입장하였습니다.");
        
        return ChatResponseDto.ChatMessageDto.builder()
                .messageId(null)
                .message(message)
                .nickname(member.getNickname())
                .profileImg(member.getProfile().getImgUrl())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 퇴장
    @MessageMapping("/exit/{studyId}")
    @SendTo("/topic/greetings/{studyId}")
    public ChatResponseDto.ChatMessageDto exit(@CurrentMember Member member) {
        String message = HtmlUtils.htmlEscape(member.getNickname() + "님이 퇴장하였습니다.");
        
        return ChatResponseDto.ChatMessageDto.builder()
                .messageId(null)
                .message(message)
                .nickname(member.getNickname())
                .profileImg(member.getProfile().getImgUrl())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 채팅 전송
    @MessageMapping("/chat/{studyId}")
    @SendTo("/topic/greetings/{studyId}")
    public ChatResponseDto.ChatMessageDto chat(String message, MultipartFile file, @CurrentMember Member member) {
        return chatMessageService.saveChatMessage(message, file, member);
    }

}
