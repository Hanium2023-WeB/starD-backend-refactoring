package com.web.stard.domain.study.domain.entity;


import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.study.domain.enums.ApplicationStatus;
import com.web.stard.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyApplicant extends BaseEntity {

    @Builder
    public StudyApplicant(
            Study study,
            Member member,
            String introduction
    ) {
        this.study = study;
        this.member = member;
        this.introduction = introduction;
        this.status = ApplicationStatus.PENDING;
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
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(length = 500, nullable = false)
    private String introduction;

    public void updateStatus(ApplicationStatus status) {
        this.status = status;
    }

}
