package com.web.stard.domain.teamBlog.service;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.teamBlog.domain.dto.request.ScheduleRequestDto;
import com.web.stard.domain.teamBlog.domain.dto.response.ScheduleResponseDto;

import java.util.List;

public interface ScheduleService {
    ScheduleResponseDto.ScheduleDto createSchedule(Long studyId, ScheduleRequestDto.CreateDto requestDto, Member member);

    ScheduleResponseDto.ScheduleDto updateSchedule(Long studyId, Long scheduleId, ScheduleRequestDto.UpdateDto requestDto, Member member);

    Long deleteSchedule(Long studyId, Long scheduleId, Member member);

    List<ScheduleResponseDto.ScheduleDto> getAllScheduleListByStudy(Long studyId, Member member, int year, int month);

    List<ScheduleResponseDto.ScheduleDto> getMemberScheduleList(Member member, int year, int month);
}
