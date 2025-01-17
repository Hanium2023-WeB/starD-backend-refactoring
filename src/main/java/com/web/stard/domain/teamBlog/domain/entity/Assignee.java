package com.web.stard.domain.teamBlog.domain.entity;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.study.domain.entity.StudyMember;
import com.web.stard.domain.teamBlog.domain.entity.ToDo;
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
    private StudyMember studyMember;

    @Column(name = "to_do_status", nullable = false)
    private boolean toDoStatus; // TO DO 상태 (false 미완료, true 완료)

    public void deleteAssignee() {
        this.toDo = null;
    }

    public void updateToDoStatus(boolean status) {
        toDoStatus = status;
    }

    public void updateMemberToDeleted(StudyMember studyMember) {
        this.studyMember = studyMember;
    }
}
