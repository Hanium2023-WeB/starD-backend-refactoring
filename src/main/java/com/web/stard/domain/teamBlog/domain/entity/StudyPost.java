package com.web.stard.domain.teamBlog.domain.entity;

import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyMember;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class StudyPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_post_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_member_id", nullable = false)
    private StudyMember studyMember; // TODO: StudyMember로 변경

    @Column(nullable = false)
    private String title;   // 제목

    @Column(nullable = false)
    private String content; // 내용

    @Column(name = "file_url")
    private String fileUrl;  // 파일 경로

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type")
    private PostType postType; // 게시글 타입: STUDYPOST

    @Column(nullable = false)
    private int hit;    // 조회수
}
