package com.web.stard.domain.chat.repository;

import com.web.stard.domain.chat.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByStudyId(Long studyId);
}
