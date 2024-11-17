package com.web.stard.domain.study.domain.dto.response;

import com.web.stard.domain.study.domain.entity.Assignee;
import com.web.stard.domain.study.domain.entity.ToDo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class ToDoResponseDto {

    @Getter
    @Builder
    public static class AssigneeDto {
        private Long assigneeId;
        private String nickname;
        private boolean toDoStatus;

        public static AssigneeDto from (Assignee assignee) {
            return AssigneeDto.builder()
                    .assigneeId(assignee.getId())
                    .nickname(assignee.getMember().getNickname())
                    .toDoStatus(assignee.isToDoStatus())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ToDoDto {
        private Long toDoId;
        private String task;
        private LocalDate dueDate;
        private Long studyId;
        private boolean toDoStatus;
        private List<AssigneeDto> assignees;

        public static ToDoDto from (ToDo toDo, List<Assignee> assigneeList) {
            List<AssigneeDto> assignees = assigneeList.stream()
                                            .map(assignee -> AssigneeDto.from(assignee)).toList();

            return ToDoDto.builder()
                    .toDoId(toDo.getId())
                    .task(toDo.getTask())
                    .dueDate(toDo.getDueDate())
                    .studyId(toDo.getStudy().getId())
                    .toDoStatus(toDo.isToDoStatus())
                    .assignees(assignees)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class MemberToDoDto {
        private Long toDoId;
        private String task;
        private LocalDate dueDate;
        private Long studyId;
        private boolean toDoStatus;

        public static MemberToDoDto of (Assignee assignee) {
            return MemberToDoDto.builder()
                    .toDoId(assignee.getToDo().getId())
                    .task(assignee.getToDo().getTask())
                    .dueDate(assignee.getToDo().getDueDate())
                    .studyId(assignee.getToDo().getStudy().getId())
                    .toDoStatus(assignee.isToDoStatus())
                    .build();
        }
    }
}
