package com.web.stard.domain.teamBlog.domain.entity;

import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyMember;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id", nullable = false)
    private Long id;

    @Column(name = "star_rating", nullable = false)
    private double starRating; // 별점

    @Column(name = "star_reason", nullable = false)
    private String starReason; // 별점 사유

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_member_id", nullable = false)
    private StudyMember studyMember; // 작성자 (평가한 회원)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private StudyMember target; // 평가 대상 회원


    public void updateStarReason(String starReason) {
        if (!this.starReason.equals(starReason)) {
            this.starReason = starReason;
        }
    }

    public void updateMemberToDeleted(StudyMember studyMember) {
        this.studyMember = studyMember;
    }
}
