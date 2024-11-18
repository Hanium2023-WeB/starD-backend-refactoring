package com.web.stard.domain.study.api;

import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.study.domain.dto.request.ToDoRequestDto;
import com.web.stard.domain.study.domain.dto.response.ToDoResponseDto;
import com.web.stard.domain.study.service.ToDoService;
import com.web.stard.global.domain.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/studies/{studyId}/to-dos")
@RequiredArgsConstructor
public class ToDoController {

    private final ToDoService toDoService;

    @Operation(summary = "ToDo 등록")
    @PostMapping
    public ResponseEntity<ToDoResponseDto.ToDoDto> createToDo(@CurrentMember Member member,
                                                              @PathVariable(name = "studyId") Long studyId,
                                                              @Valid @RequestBody ToDoRequestDto.CreateDto requestDto) {
        return ResponseEntity.ok(toDoService.createToDo(studyId, requestDto, member));
    }

    @Operation(summary = "ToDo 업무 내용 수정")
    @PutMapping("/{toDoId}/task")
    public ResponseEntity<ToDoResponseDto.ToDoDto> updateTask(@CurrentMember Member member,
                                                              @PathVariable(name = "studyId") Long studyId,
                                                              @PathVariable(name = "toDoId") Long toDoId,
                                                              @Valid @RequestBody ToDoRequestDto.TaskDto requestDto) {
        return ResponseEntity.ok(toDoService.updateTask(studyId, toDoId, requestDto, member));
    }

    @Operation(summary = "ToDo 마감일 수정")
    @PutMapping("/{toDoId}/dueDate")
    public ResponseEntity<ToDoResponseDto.ToDoDto> updateDueDate(@CurrentMember Member member,
                                                                 @PathVariable(name = "studyId") Long studyId,
                                                                 @PathVariable(name = "toDoId") Long toDoId,
                                                                 @Valid @RequestBody ToDoRequestDto.DueDateDto requestDto) {
        return ResponseEntity.ok(toDoService.updateDueDate(studyId, toDoId, requestDto, member));
    }

    @Operation(summary = "ToDo 담당자 수정")
    @PutMapping("/{toDoId}/assignees")
    public ResponseEntity<ToDoResponseDto.ToDoDto> updateAssignee(@CurrentMember Member member,
                                                                  @PathVariable(name = "studyId") Long studyId,
                                                                  @PathVariable(name = "toDoId") Long toDoId,
                                                                  @Valid @RequestBody ToDoRequestDto.AssigneeDto requestDto) {
        return ResponseEntity.ok(toDoService.updateAssignee(studyId, toDoId, requestDto, member));
    }

    @Operation(summary = "ToDo 상태 변화")
    @PutMapping("/{toDoId}/{assigneeId}")
    public ResponseEntity<ToDoResponseDto.ToDoDto> updateStatus(@CurrentMember Member member,
                                                                @PathVariable(name = "studyId") Long studyId,
                                                                @PathVariable(name = "toDoId") Long toDoId,
                                                                @PathVariable(name = "assigneeId") Long assigneeId,
                                                                @RequestParam(name = "status") boolean status) {
        return ResponseEntity.ok(toDoService.updateTodoStatus(studyId, toDoId, assigneeId, status, member));
    }

    @Operation(summary = "ToDo 삭제")
    @DeleteMapping("/{toDoId}")
    public ResponseEntity<Long> deleteToDo(@CurrentMember Member member,
                                           @PathVariable(name = "studyId") Long studyId,
                                           @PathVariable(name = "toDoId") Long toDoId) {
        return ResponseEntity.ok(toDoService.deleteToDo(studyId, toDoId, member));
    }

    @Operation(summary = "스터디 별 전체 ToDo 조회 - 월 단위")
    @GetMapping
    public ResponseEntity<List<ToDoResponseDto.ToDoDto>> getStudyToDoList(@CurrentMember Member member,
                                                                          @PathVariable(name = "studyId") Long studyId,
                                                                          @RequestParam(name = "year") int year,
                                                                          @RequestParam(name = "month") int month) {
        return ResponseEntity.ok(toDoService.getAllToDoListByStudy(studyId, member, year, month));
    }
}
