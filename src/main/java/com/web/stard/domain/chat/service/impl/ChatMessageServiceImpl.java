package com.web.stard.domain.chat.service.impl;

import com.web.stard.domain.chat.domain.dto.response.ChatResponseDto;
import com.web.stard.domain.chat.domain.entity.ChatMessage;
import com.web.stard.domain.chat.domain.enums.MessageType;
import com.web.stard.domain.chat.repository.ChatMessageRepository;
import com.web.stard.domain.chat.service.ChatMessageService;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.study.domain.entity.StudyMember;
import com.web.stard.domain.study.repository.StudyMemberRepository;
import com.web.stard.global.config.aws.S3Manager;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final StudyMemberRepository studyMemberRepository;

    /**
     * 채팅 전송
     * @param message 전송할 메시지
     * @return ChatMessageDto messageId 메시지 id, message 메시지 내용, nickname 작성자 닉네임, createdAt 작성 일시
     */
    @Override
    @Transactional
    public ChatResponseDto.ChatMessageDto saveChatMessage(String message, Member member) {
        StudyMember studyMember = studyMemberRepository.findStudyMemberByMember(member)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_MEMBER_NOT_FOUND));

        ChatMessage chatMessage = ChatMessage.builder()
                .message(message)
                .messageType(MessageType.TALK)
                .studyMember(studyMember)
                .build();

        ChatMessage savedChatMessage =  chatMessageRepository.save(chatMessage);
        return ChatResponseDto.ChatMessageDto.of(savedChatMessage, member);
    }

}
