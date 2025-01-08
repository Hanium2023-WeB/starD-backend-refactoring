package com.web.stard.domain.teamBlog.service;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.teamBlog.domain.dto.request.ToDoRequestDto;
import com.web.stard.domain.teamBlog.domain.dto.response.ToDoResponseDto;

import java.util.List;

public interface ToDoService {
    ToDoResponseDto.ToDoDto createToDo(Long studyId, ToDoRequestDto.CreateDto requestDto, Member member);

    ToDoResponseDto.ToDoDto updateToDo(Long studyId, Long toDoId, ToDoRequestDto.CreateDto requestDto, Member member);

    ToDoResponseDto.ToDoDto updateTodoStatus(Long studyId, Long toDoId, Long assigneeId, boolean status, Member member);

    Long deleteToDo(Long studyId, Long toDoId, Member member);

    List<ToDoResponseDto.ToDoDto> getAllToDoListByStudy(Long studyId, Member member, int year, int month);

    List<ToDoResponseDto.MemberToDoDto> getMemberToDoList(Member member, int year, int month);

    List<ToDoResponseDto.MemberToDoDto> getMemberToDoListByStudy(Long studyId, Member member, int year, int month);
}
