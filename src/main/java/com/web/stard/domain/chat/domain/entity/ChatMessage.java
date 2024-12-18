package com.web.stard.domain.chat.domain.entity;

import com.web.stard.domain.chat.domain.enums.MessageType;
import com.web.stard.domain.study.domain.entity.StudyMember;
import com.web.stard.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id", nullable = false)
    private Long id;

    private String message;

    @Column(name = "img_url")
    private String imgUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_member_id")
    private StudyMember studyMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;
}
