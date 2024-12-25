package com.web.stard.domain.notification.domain.entity;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.notification.domain.enums.NotificationType;
import com.web.stard.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Builder
    public Notification(String title, String body, Long targetId,
                        NotificationType type, Member receiver) {
        this.title = title;
        this.body = body;
        this.type = type;
        this.read = false;
        this.receiver = receiver;
        this.targetId = targetId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String body;

    private boolean read;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "member_id")
    private Member receiver;

    @Column(nullable = false)
    private Long targetId;

    public void updateReadStatus() {
        this.read = true;
    }

}
