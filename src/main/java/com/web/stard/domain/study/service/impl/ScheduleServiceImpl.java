package com.web.stard.domain.study.service.impl;

import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.study.domain.dto.request.ScheduleRequestDto;
import com.web.stard.domain.study.domain.dto.response.ScheduleResponseDto;
import com.web.stard.domain.study.domain.entity.Schedule;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.repository.ScheduleRepository;
import com.web.stard.domain.study.service.ScheduleService;
import com.web.stard.domain.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final StudyService studyService;

    /**
     * 스터디 - 일정 등록
     *
     * @param studyId 해당 study 고유 id
     * @param member 로그인 회원
     * @param requestDto title 일정명, color 달력 표시 색상, startDate 일정일
     *
     * @return ScheduleDto scheduleId, title 일정명, color 달력 표시 색상, startDate 일정일, studyId
     */
    @Transactional
    @Override
    public ScheduleResponseDto.ScheduleDto createSchedule(Long studyId, ScheduleRequestDto.CreateDto requestDto, Member member) {
        Study study = studyService.findById(studyId);
        studyService.isStudyInProgress(study);
        studyService.isStudyMember(study, member);

        Schedule schedule = scheduleRepository.save(requestDto.toEntity(study));

        return ScheduleResponseDto.ScheduleDto.from(schedule);
    }


}
