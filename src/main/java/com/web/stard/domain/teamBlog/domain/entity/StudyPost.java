package com.web.stard.domain.teamBlog.domain.entity;

import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyMember;
import com.web.stard.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class StudyPost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_post_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_member_id", nullable = false)
    private StudyMember studyMember;

    @Column(nullable = false)
    private String title;   // 제목

    @Column(nullable = false)
    private String content; // 내용

    @OneToMany(mappedBy = "studyPost", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<StudyPostFile> files;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type")
    private PostType postType; // 게시글 타입: STUDYPOST

    @Column(nullable = false)
    private int hit;    // 조회수


    public void addFile(StudyPostFile studyPostFile) {
        if (this.files == null) {
            this.files = new ArrayList<>();
        }
        this.files.add(studyPostFile);
    }

    public void updateStudyPost(String title, String content) {
        if (!this.title.equals(title)) {
            this.title = title;
        }

        if (!this.content.equals(content)) {
            this.content = content;
        }
    }

    public void incrementHitCount() {
        this.hit++;
    }
}
