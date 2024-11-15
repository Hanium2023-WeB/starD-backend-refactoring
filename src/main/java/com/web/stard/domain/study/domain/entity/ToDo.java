package com.web.stard.domain.study.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class ToDo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "to_do_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String task; // 담당 업무

    @Column(name = "due_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate; // 마감일

    @Column(name = "to_do_status", nullable = false)
    private boolean toDoStatus; // TO DO 상태 (false 미완료, true 완료)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @OneToMany(mappedBy = "toDo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignee> assignees = new ArrayList<>();


    public void updateTask(String task) {
        this.task = task;
    }

    public void updateDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void updateToDoStatus(boolean status) {
        toDoStatus = status;
    }
}
