package com.web.stard.domain.teamBlog.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class StudyPostFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_post_file_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_post_id", nullable = false)
    private StudyPost studyPost;

    @Column(name = "file_name")
    private String fileName;  // 파일명

    @Column(name = "file_url")
    private String fileUrl;  // 파일 경로


    public void deleteStudyPost() {
        this.studyPost = null;
    }
}
