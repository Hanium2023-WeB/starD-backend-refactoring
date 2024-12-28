package com.web.stard.domain.chat.api;

import com.web.stard.domain.chat.domain.dto.response.ChatResponseDto;
import com.web.stard.domain.chat.domain.enums.MessageType;
import com.web.stard.domain.chat.service.ChatMessageService;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.global.config.security.JwtTokenProvider;
import com.web.stard.global.domain.CurrentMember;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class GreetingController {

    private final ChatMessageService chatMessageService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public Authentication getUserAuthenticationFromToken(String accessToken) {
        jwtTokenProvider.validateToken(accessToken);
        return jwtTokenProvider.getAuthentication(accessToken);
    }

    // 입장
    @MessageMapping("/enter/{studyId}")
    @SendTo("/topic/greetings/{studyId}")
    public ChatResponseDto.ChatMessageDto enter(SimpMessageHeaderAccessor session) {
        Authentication authentication = getUserAuthenticationFromToken(session.getFirstNativeHeader("Authorization"));
        Member member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String message = HtmlUtils.htmlEscape(member.getNickname() + "님이 입장하였습니다.");

        return ChatResponseDto.ChatMessageDto.builder()
                .messageId(null)
                .message(message)
                .messageType(MessageType.GREETING)
                .nickname(member.getNickname())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 퇴장
    @MessageMapping("/exit/{studyId}")
    @SendTo("/topic/greetings/{studyId}")
    public ChatResponseDto.ChatMessageDto exit(SimpMessageHeaderAccessor session) {
        Authentication authentication = getUserAuthenticationFromToken(session.getFirstNativeHeader("Authorization"));
        Member member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String message = HtmlUtils.htmlEscape(member.getNickname() + "님이 퇴장하였습니다.");

        return ChatResponseDto.ChatMessageDto.builder()
                .messageId(null)
                .message(message)
                .messageType(MessageType.GREETING)
                .nickname(member.getNickname())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 채팅 전송
    @MessageMapping("/chat/{studyId}")
    @SendTo("/topic/greetings/{studyId}")
    public ChatResponseDto.ChatMessageDto chat(String message, SimpMessageHeaderAccessor session) {
        Authentication authentication = getUserAuthenticationFromToken(session.getFirstNativeHeader("Authorization"));
        Member member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return chatMessageService.saveChatMessage(message, member);
    }

}
