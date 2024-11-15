package com.web.stard.domain.study.domain.entity;

import com.web.stard.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Assignee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignee_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_do_id")
    private ToDo toDo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_member_id")
    private Member member; // TODO: StudyMember로 변경

    @Column(name = "to_do_status", nullable = false)
    private boolean toDoStatus; // TO DO 상태 (false 미완료, true 완료)
}
