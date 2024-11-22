package com.web.stard.domain.study.domain.entity;

import com.web.stard.domain.member.domain.enums.InterestField;
import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.member.domain.entity.Member;
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
            LocalDate recruitmentDeadline,
            ActivityType activityType,
            String city,
            String district,
            String tagText,
            InterestField field
    ) {
        this.title = title;
        this.content = content;
        this.capacity = capacity;
        this.tagText = tagText;
        this.hit = 0L;
        this.activityStart = activityStart;
        this.activityDeadline = activityDeadline;
        this.recruitmentDeadline = recruitmentDeadline;
        this.postType = PostType.STUDY;
        this.progressType = ProgressType.NOT_STARTED;
        this.activityType = activityType;
        this.field = field;

        this.tags = new ArrayList<>();
        this.city = (activityType.equals(ActivityType.OFFLINE)) ? null : city;
        this.district = (activityType.equals(ActivityType.OFFLINE)) ? null : district;
        this.recruitmentType = RecruitmentType.RECRUITING;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
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

    @Column(name = "recruitment_deadline")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate recruitmentDeadline;

    @Column(name = "study_field", nullable = false)
    @Enumerated(EnumType.STRING)
    private InterestField field;

    @OneToMany(mappedBy = "study", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<StudyTag> tags;

    public void updateMember(Member member) {
        this.member = member;
    }

    public void updateStudy(Study updateStudy) {
        if (!this.title.equals(updateStudy.getTitle())) {
            this.title = updateStudy.getTitle();
        }

        if (!this.content.equals(updateStudy.getContent())) {
            this.content = updateStudy.getContent();
        }

        if (!(this.capacity == updateStudy.getCapacity())) {
            this.capacity = updateStudy.getCapacity();
        }

        if (!this.activityType.equals(updateStudy.getActivityType())) {
            this.activityType = updateStudy.getActivityType();
        }

        if (!this.city.equals(updateStudy.getCity())) {
            this.city = updateStudy.getCity();
        }

        if (!this.district.equals(updateStudy.getDistrict())) {
            this.district = updateStudy.getDistrict();
        }

        if (!this.tagText.equals(updateStudy.getTagText())) {
            this.tagText = updateStudy.getTagText();
        }

        if (!this.activityStart.equals(updateStudy.getActivityStart())) {
            this.activityStart = updateStudy.getActivityStart();
        }

        if (!this.activityDeadline.equals(updateStudy.getActivityDeadline())) {
            this.activityDeadline = updateStudy.getActivityDeadline();
        }

        if (!this.recruitmentDeadline.equals(updateStudy.getRecruitmentDeadline())) {
            this.recruitmentDeadline = updateStudy.getRecruitmentDeadline();
        }

        if (!this.recruitmentType.equals(RecruitmentType.RECRUITING)) {
            this.recruitmentType = RecruitmentType.RECRUITING;
        }

        if (!this.field.equals(updateStudy.getField())) {
            this.field = updateStudy.getField();
        }
    }

    public void addTags(List<StudyTag> studyTags) {
        if (studyTags.isEmpty()) {
            studyTags = new ArrayList<>();
        }
        this.tags.addAll(studyTags);
    }

    public void updateRecruitmentType(RecruitmentType recruitmentType) {
        this.recruitmentType = recruitmentType;
    }
}
