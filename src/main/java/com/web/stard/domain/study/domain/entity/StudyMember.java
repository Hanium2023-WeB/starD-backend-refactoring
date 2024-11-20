package com.web.stard.domain.study.domain.entity;


import com.web.stard.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyMember {

    @Builder
    public StudyMember(
            Study study,
            Member member
    ) {
        this.study = study;
        this.member = member;
        this.studyRemoved = false;
        this.commentNotification = false;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private boolean commentNotification;

    @Column(nullable = false)
    private boolean studyRemoved;

    public void updateStudyRemoved(boolean studyRemoved) {
        this.studyRemoved = studyRemoved;
    }

}
