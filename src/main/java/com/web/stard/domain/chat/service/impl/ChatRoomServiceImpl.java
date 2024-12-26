package com.web.stard.domain.chat.service.impl;

import com.web.stard.domain.chat.domain.dto.response.ChatResponseDto;
import com.web.stard.domain.chat.domain.entity.ChatMessage;
import com.web.stard.domain.chat.domain.entity.ChatRoom;
import com.web.stard.domain.chat.repository.ChatMessageRepository;
import com.web.stard.domain.chat.repository.ChatRoomRepository;
import com.web.stard.domain.chat.service.ChatRoomService;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.repository.StudyRepository;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final StudyRepository studyRepository;


    /**
     * 채팅방 생성
     * @param studyId 채팅방 생성할 스터디 id
     * @return createChatRoomDto chatRoomId 생성된 채팅방 id, studyId 스터디 id
     */
    @Override
    @Transactional
    public ChatResponseDto.createChatRoomDto createRoom(Long studyId) {
        // 이미 채팅방이 존재하는지 확인
        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findByStudyId(studyId);

        if (existingChatRoom.isPresent()) {
            throw new CustomException(ErrorCode.CHAT_ROOM_ALREADY_EXISTS);
        }

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));

        ChatRoom chatRoom = ChatRoom.builder()
                .study(study)
                .build();

        return ChatResponseDto.createChatRoomDto.from(chatRoomRepository.save(chatRoom));
    }

    /**
     * 채팅 내역 조회
     * @param studyId 채팅 내역을 조회할 스터디 id
     * @return ChatMessageListDto chatRoomId 조회한 채팅방 id, chatMessages 채팅 내역 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public ChatResponseDto.ChatMessageListDto getChatHistory(Long studyId, Member member) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findByStudyId(study.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoomId(chatRoom.getId());

        return ChatResponseDto.ChatMessageListDto.of(chatRoom, chatMessages, member);
    }

}
