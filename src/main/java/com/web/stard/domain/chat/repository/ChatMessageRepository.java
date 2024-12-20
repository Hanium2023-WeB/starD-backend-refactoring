package com.web.stard.domain.chat.repository;

import com.web.stard.domain.chat.domain.entity.ChatMessage;
import com.web.stard.domain.study.domain.entity.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByChatRoomId(Long id);

    List<ChatMessage> findAllByStudyMember(StudyMember studyMember);
}
