package com.web.stard.domain.teamBlog.domain.dto.response;

import com.web.stard.domain.teamBlog.domain.entity.Assignee;
import com.web.stard.domain.teamBlog.domain.entity.ToDo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class ToDoResponseDto {

    @Getter
    @Builder
    public static class AssigneeDto {
        @Schema(description = "담당자 고유 id")
        private Long assigneeId;

        @Schema(description = "담당자 닉네임")
        private String nickname;

        @Schema(description = "담당자 이메일")
        private String email;

        @Schema(description = "담당자 - 투두 완료 상태")
        private boolean toDoStatus;

        public static AssigneeDto from (Assignee assignee) {
            return AssigneeDto.builder()
                    .assigneeId(assignee.getId())
                    .email(assignee.getStudyMember().getMember().getEmail())
                    .nickname(assignee.getStudyMember().getMember().getNickname())
                    .toDoStatus(assignee.getToDoStatus())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ToDoDto {
        @Schema(description = "투두 고유 id")
        private Long toDoId;

        @Schema(description = "담당 업무")
        private String task;

        @Schema(description = "마감일")
        private LocalDate dueDate;

        @Schema(description = "해당 스터디 id")
        private Long studyId;

        @Schema(description = "투두 완료 상태 (모든 담당자 완료 시 true)")
        private boolean toDoStatus;

        @Schema(description = "담당자 리스트")
        private List<AssigneeDto> assignees;

        public static ToDoDto from (ToDo toDo, List<Assignee> assigneeList) {
            List<AssigneeDto> assignees = assigneeList.stream()
                                            .map(assignee -> AssigneeDto.from(assignee)).toList();

            return ToDoDto.builder()
                    .toDoId(toDo.getId())
                    .task(toDo.getTask())
                    .dueDate(toDo.getDueDate())
                    .studyId(toDo.getStudy().getId())
                    .toDoStatus(toDo.getToDoStatus())
                    .assignees(assignees)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class MemberToDoDto {
        @Schema(description = "투두 고유 id")
        private Long toDoId;

        @Schema(description = "담당 업무")
        private String task;

        @Schema(description = "마감일")
        private LocalDate dueDate;

        @Schema(description = "해당 스터디 id")
        private Long studyId;

        @Schema(description = "회원 - 투두 완료 상태")
        private boolean toDoStatus;

        public static MemberToDoDto of (Assignee assignee) {
            return MemberToDoDto.builder()
                    .toDoId(assignee.getToDo().getId())
                    .task(assignee.getToDo().getTask())
                    .dueDate(assignee.getToDo().getDueDate())
                    .studyId(assignee.getToDo().getStudy().getId())
                    .toDoStatus(assignee.getToDoStatus())
                    .build();
        }
    }
}
