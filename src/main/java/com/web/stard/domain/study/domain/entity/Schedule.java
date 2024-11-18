package com.web.stard.domain.study.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title; // 일정명

    @Column(nullable = false)
    private String color; // 달력 표시 색상

    @Column(name = "start_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate; // 일정일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    public void updateSchedule(String title, String color) {
        this.title = title;
        this.color = color;
    }
}
