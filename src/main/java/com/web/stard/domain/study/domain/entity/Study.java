package com.web.stard.domain.study.domain.entity;

import com.web.stard.domain.board.global.domain.enums.PostType;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.study.domain.enums.ActivityType;
import com.web.stard.domain.study.domain.enums.ProgressType;
import com.web.stard.domain.study.domain.enums.RecruitmentType;
import com.web.stard.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study extends BaseEntity {

    @Builder
    public Study(
            String title,
            String content,
            int capacity,
            LocalDate activityStart,
            LocalDate activityDeadline,
            LocalDate recruitmentStart,
            LocalDate recruitmentDeadline,
            ActivityType activityType,
            String city,
            String district,
            String tagText
    ) {
        this.title = title;
        this.content = content;
        this.capacity = capacity;
        this.tagText = tagText;
        this.hit = 0L;
        this.activityStart = activityStart;
        this.activityDeadline = activityDeadline;
        this.recruitmentStart = recruitmentStart;
        this.recruitmentDeadline = recruitmentDeadline;
        this.postType = PostType.STUDY;
        this.progressType = ProgressType.NOT_STARTED;
        this.activityType = activityType;

        this.tags = new ArrayList<>();
        this.city = (activityType.equals(ActivityType.OFFLINE)) ? null : city;
        this.district = (activityType.equals(ActivityType.OFFLINE)) ? null : district;
        this.recruitmentType = (LocalDate.now().isBefore(recruitmentStart)) ? RecruitmentType.PRE_RECRUITMENT : RecruitmentType.RECRUITING;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "longtext", length = 1000, nullable = false)
    private String content;

    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type")
    private PostType postType;

    @Enumerated(EnumType.STRING)
    @Column(name = "progress_type")
    private ProgressType progressType;

    @Enumerated(EnumType.STRING)
    @Column(name = "recruitment_type")
    private RecruitmentType recruitmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type")
    private ActivityType activityType;

    private String tagText;

    private String city;

    private String district;

    private Long hit;

    @Column(name = "activity_start")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate activityStart;

    @Column(name = "activity_deadline")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate activityDeadline;

    @Column(name = "recruitment_start")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate recruitmentStart;

    @Column(name = "recruitment_deadline")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate recruitmentDeadline;

    @OneToMany(mappedBy = "study", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<StudyTag> tags;

    public void updateMember(Member member) {
        this.member = member;
    }

    public void updateStudy(String title, String content) {
        if (!this.title.equals(title)) {
            this.title = title;
        }

        if (!this.content.equals(content)) {
            this.content = content;
        }
    }

    public void addTags(List<StudyTag> studyTags) {
        if (studyTags.isEmpty()) {
            studyTags = new ArrayList<>();
        }

        this.tags.addAll(studyTags);
    }
}
