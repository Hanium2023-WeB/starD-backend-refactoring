package com.web.stard.domain.study.service;

import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.study.domain.dto.request.ToDoRequestDto;
import com.web.stard.domain.study.domain.dto.response.ToDoResponseDto;

public interface ToDoService {
    ToDoResponseDto.ToDoDto createToDo(Long studyId, ToDoRequestDto.CreateDto requestDto, Member member);

    ToDoResponseDto.ToDoDto updateTask(Long studyId, Long toDoId, ToDoRequestDto.TaskDto requestDto, Member member);

    ToDoResponseDto.ToDoDto updateDueDate(Long studyId, Long toDoId, ToDoRequestDto.DueDateDto requestDto, Member member);

    ToDoResponseDto.ToDoDto updateAssignee(Long studyId, Long toDoId, ToDoRequestDto.AssigneeDto requestDto, Member member);
}
