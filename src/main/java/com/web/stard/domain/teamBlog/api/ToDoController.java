package com.web.stard.domain.teamBlog.api;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.teamBlog.domain.dto.request.ToDoRequestDto;
import com.web.stard.domain.teamBlog.domain.dto.response.ToDoResponseDto;
import com.web.stard.domain.teamBlog.service.ToDoService;
import com.web.stard.global.domain.CurrentMember;
import com.web.stard.global.exception.ApiErrorCodeExamples;
import com.web.stard.global.exception.error.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/studies/{studyId}/to-dos")
@RequiredArgsConstructor
@Tag(name = "studies-to-dos", description = "스터디 팀블로그 - to do 관련 API")
public class ToDoController {

    private final ToDoService toDoService;

    @Operation(summary = "ToDo 등록")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND, ErrorCode.STUDY_NOT_IN_PROGRESS,
            ErrorCode.STUDY_NOT_MEMBER, ErrorCode.STUDY_MEMBER_NOT_FOUND
    })
    @PostMapping
    public ResponseEntity<ToDoResponseDto.ToDoDto> createToDo(@CurrentMember Member member,
                                                              @PathVariable(name = "studyId") Long studyId,
                                                              @Valid @RequestBody ToDoRequestDto.CreateDto requestDto) {
        return ResponseEntity.ok(toDoService.createToDo(studyId, requestDto, member));
    }

    @Operation(summary = "ToDo 수정")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND, ErrorCode.STUDY_NOT_IN_PROGRESS, ErrorCode.STUDY_NOT_MEMBER,
            ErrorCode.STUDY_MEMBER_NOT_FOUND, ErrorCode.STUDY_TODO_BAD_REQUEST
    })
    @PutMapping("/{toDoId}")
    public ResponseEntity<ToDoResponseDto.ToDoDto> updateToDo(@CurrentMember Member member,
                                                              @PathVariable(name = "studyId") Long studyId,
                                                              @PathVariable(name = "toDoId") Long toDoId,
                                                              @Valid @RequestBody ToDoRequestDto.CreateDto requestDto) {
        return ResponseEntity.ok(toDoService.updateToDo(studyId, toDoId, requestDto, member));
    }

    @Operation(summary = "ToDo 상태 변화")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND, ErrorCode.STUDY_NOT_IN_PROGRESS,
            ErrorCode.STUDY_NOT_MEMBER, ErrorCode.STUDY_TODO_BAD_REQUEST
    })
    @PutMapping("/{toDoId}/{assigneeId}")
    public ResponseEntity<ToDoResponseDto.ToDoDto> updateStatus(@CurrentMember Member member,
                                                                @PathVariable(name = "studyId") Long studyId,
                                                                @PathVariable(name = "toDoId") Long toDoId,
                                                                @PathVariable(name = "assigneeId") Long assigneeId,
                                                                @RequestParam(name = "status") boolean status) {
        return ResponseEntity.ok(toDoService.updateTodoStatus(studyId, toDoId, assigneeId, status, member));
    }

    @Operation(summary = "ToDo 삭제")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND, ErrorCode.STUDY_NOT_IN_PROGRESS,
            ErrorCode.STUDY_NOT_MEMBER, ErrorCode.STUDY_TODO_BAD_REQUEST
    })
    @DeleteMapping("/{toDoId}")
    public ResponseEntity<Long> deleteToDo(@CurrentMember Member member,
                                           @PathVariable(name = "studyId") Long studyId,
                                           @PathVariable(name = "toDoId") Long toDoId) {
        return ResponseEntity.ok(toDoService.deleteToDo(studyId, toDoId, member));
    }

    @Operation(summary = "스터디 별 전체 ToDo 조회 - 월 단위")
    @ApiErrorCodeExamples({
            ErrorCode.STUDY_NOT_FOUND, ErrorCode.STUDY_NOT_MEMBER
    })
    @GetMapping
    public ResponseEntity<List<ToDoResponseDto.ToDoDto>> getStudyToDoList(@CurrentMember Member member,
                                                                          @PathVariable(name = "studyId") Long studyId,
                                                                          @RequestParam(name = "year") int year,
                                                                          @RequestParam(name = "month") int month) {
        return ResponseEntity.ok(toDoService.getAllToDoListByStudy(studyId, member, year, month));
    }
}
