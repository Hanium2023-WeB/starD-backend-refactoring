package com.web.stard.domain.board.study.domain;

import com.web.stard.domain.board.global.domain.enums.PostType;
import com.web.stard.domain.board.study.domain.enums.ProgressStatus;
import com.web.stard.domain.board.study.domain.enums.RecruitStatus;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Study extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member recruiter; // 작성자

    private String tags; // 태그들

    @Column(nullable = false, name = "on_off")
    private String onOff; // 온/오프/무관

    @Column(nullable = false, name = "activity_start")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate activityStart; // 활동 시작일

    @Column(nullable = false, name = "activity_deadline")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate activityDeadline; // 활동 마감일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "recruit_status")
    private RecruitStatus recruitStatus; // 스터디 모집 현황 (모집 중, 모집 완료)

    @Enumerated(EnumType.STRING)
    @Column(name = "progress_status")
    private ProgressStatus progressStatus; // 스터디 진행 상황 (진행 중, 진행 완료, 중단 등)

    @CreatedDate
    @Column(name = "recruitment_start", updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate recruitmentStart; // 모집 시작일

    @Column(nullable = false, name = "recruitment_deadline")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate recruitmentDeadline; // 모집 마감일

    @Column(nullable = false)
    private int hit; // 조회수

    @Column(nullable = false)
    private int capacity; // 모집 인원

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false)
    private String content; // 내용

    @Enumerated(EnumType.STRING)
    private PostType type; // 스터디 타입 (?)

    private String city; // 시

    private String district; // 구

}
