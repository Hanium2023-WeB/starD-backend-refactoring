package com.web.stard.domain.study.service;

import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.study.domain.dto.request.ToDoRequestDto;
import com.web.stard.domain.study.domain.dto.response.ToDoResponseDto;

public interface ToDoService {
    ToDoResponseDto.ToDoDto createToDo(Long studyId, ToDoRequestDto.ToDoCreateDto requestDto, Member member);
}
