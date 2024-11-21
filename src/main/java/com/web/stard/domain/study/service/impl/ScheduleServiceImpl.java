package com.web.stard.domain.study.service.impl;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.study.domain.dto.request.ScheduleRequestDto;
import com.web.stard.domain.study.domain.dto.response.ScheduleResponseDto;
import com.web.stard.domain.study.domain.entity.Schedule;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.repository.ScheduleRepository;
import com.web.stard.domain.study.service.ScheduleService;
import com.web.stard.domain.study.service.StudyService;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final StudyService studyService;

    // id로 일정 찾기
    private Schedule findSchedule(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_SCHEDULE_BAD_REQUEST));
    }

    // 일정의 스터디랑 넘어온 스터디가 같은지 확인 (혹시 모를 오류 방지)
    private void isEqualScheduleStudyAndStudy(Study study, Schedule schedule) {
        if (study.getId() != schedule.getStudy().getId()) {
            throw new CustomException(ErrorCode.STUDY_SCHEDULE_BAD_REQUEST);
        }
    }

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

    /**
     * 스터디 - 일정 수정
     *
     * @param studyId 해당 study 고유 id
     * @param scheduleId 해당 일정 고유 id
     * @param member 로그인 회원
     * @param requestDto title 일정명, color 달력 표시 색상
     *
     * @return ScheduleDto scheduleId, title 일정명, color 달력 표시 색상, startDate 일정일, studyId
     */
    @Transactional
    @Override
    public ScheduleResponseDto.ScheduleDto updateSchedule(Long studyId, Long scheduleId, ScheduleRequestDto.UpdateDto requestDto, Member member) {
        Study study = studyService.findById(studyId);
        studyService.isStudyInProgress(study);
        studyService.isStudyMember(study, member);

        Schedule schedule = findSchedule(scheduleId);

        isEqualScheduleStudyAndStudy(study, schedule);

        schedule.updateSchedule(requestDto.getTitle(), requestDto.getColor());

        return ScheduleResponseDto.ScheduleDto.from(schedule);
    }

    /**
     * 스터디 - 일정 삭제
     *
     * @param studyId 해당 study 고유 id
     * @param scheduleId  해당 일정 고유 id
     * @param member  로그인 회원
     */
    @Transactional
    @Override
    public Long deleteSchedule(Long studyId, Long scheduleId, Member member) {
        Study study = studyService.findById(studyId);
        studyService.isStudyInProgress(study);
        studyService.isStudyMember(study, member);

        Schedule schedule = findSchedule(scheduleId);

        isEqualScheduleStudyAndStudy(study, schedule);

        scheduleRepository.delete(schedule);

        return scheduleId;
    }

    /**
     * 스터디 - 전체 일정 조회 (월 단위)
     * 사용자 - 스터디 별 일정 조회 함께 사용
     *
     * @param studyId 해당 study 고유 id
     * @param year 년도
     * @param month 월
     * @param member  로그인 회원
     *
     * @return ScheduleDto 리스트 : scheduleId, title 일정명, color 달력 표시 색상, startDate 일정일, studyId
     */
    @Transactional
    @Override
    public List<ScheduleResponseDto.ScheduleDto> getAllScheduleListByStudy(Long studyId, Member member, int year, int month) {
        Study study = studyService.findById(studyId);
        studyService.isStudyMember(study, member);

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth());

        List<Schedule> scheduleList = scheduleRepository.findAllByStudyAndStartDateBetween(study, start, end);

        return scheduleList.stream().map(ScheduleResponseDto.ScheduleDto::from).toList();
    }

    /**
     * 사용자 - 전체 일정 조회 (월 단위)
     *
     * @param member 로그인 회원
     * @param year 년도
     * @param month 월
     *
     * @return ScheduleDto 리스트 : scheduleId, title 일정명, color 달력 표시 색상, startDate 일정일, studyId
     */
    @Transactional
    @Override
    public List<ScheduleResponseDto.ScheduleDto> getMemberScheduleList(Member member, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth());

        // TODO: StudyMember 구현 시 수정
//        List<StudyMember> studies = studyMemberRepository.findByMember(member);
        List<Schedule> scheduleList = new ArrayList<>();

//        for (StudyMember study : studies) {
//            scheduleList.addAll(scheduleRepository.findAllByStudyAndStartDateBetween(study.getStudy(), start, end));
//        }

        return scheduleList.stream().map(ScheduleResponseDto.ScheduleDto::from).toList();
    }
}
