package com.web.stard.domain.board.domain.entity;

import com.web.stard.domain.board.domain.enums.Category;
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
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;   // 제목

    @Column(nullable = false)
    private String content; // 내용

    @Enumerated(EnumType.STRING)
    private Category category;  // 카테고리

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private PostType postType;  // 게시글 타입

    @Column(nullable = false)
    private int hit;    // 조회수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void updatePost(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void updateComm(String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public void incrementHitCount() {
        this.hit++;
    }
}
