package com.web.stard.domain.study.service;

import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.study.domain.dto.request.ScheduleRequestDto;
import com.web.stard.domain.study.domain.dto.response.ScheduleResponseDto;

public interface ScheduleService {
    ScheduleResponseDto.ScheduleDto createSchedule(Long studyId, ScheduleRequestDto.CreateDto requestDto, Member member);
}
