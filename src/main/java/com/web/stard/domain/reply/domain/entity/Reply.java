package com.web.stard.domain.reply.domain.entity;

import com.web.stard.domain.board.domain.enums.PostType;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Reply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content; // 내용

    @Column(name = "target_id", nullable = false)
    private Long targetId;  // 대상 게시글 id

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private PostType postType;  // 게시글 타입

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public void updateReply(String content) {
        this.content = content;
    }
}
