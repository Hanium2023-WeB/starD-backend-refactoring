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

@RestController
@RequestMapping("/to-dos")
@RequiredArgsConstructor
public class ToDoController {

    private final ToDoService toDoService;

    @Operation(summary = "ToDo 등록")
    @PostMapping("/{studyId}")
    public ResponseEntity<ToDoResponseDto.ToDoDto> createToDo(@CurrentMember Member member,
                                                              @PathVariable(name = "studyId") Long studyId,
                                                              @Valid @RequestBody ToDoRequestDto.ToDoCreateDto requestDto) {
        return ResponseEntity.ok(toDoService.createToDo(studyId, requestDto, member));
    }
}
