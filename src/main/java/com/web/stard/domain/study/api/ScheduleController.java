package com.web.stard.domain.study.api;

import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.study.domain.dto.request.ScheduleRequestDto;
import com.web.stard.domain.study.domain.dto.response.ScheduleResponseDto;
import com.web.stard.domain.study.service.ScheduleService;
import com.web.stard.global.domain.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/studies/{studyId}/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "일정 등록")
    @PostMapping
    public ResponseEntity<ScheduleResponseDto.ScheduleDto> createSchedule(@CurrentMember Member member,
                                                                          @PathVariable(name = "studyId") Long studyId,
                                                                          @Valid @RequestBody ScheduleRequestDto.CreateDto requestDto) {
        return ResponseEntity.ok(scheduleService.createSchedule(studyId, requestDto, member));
    }

    @Operation(summary = "일정 수정", description = "일정명과 색상만 변경 가능합니다. \n\n 일정일 변경 시 삭제 후 생성해주세요.")
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponseDto.ScheduleDto> updateSchedule(@CurrentMember Member member,
                                                                          @PathVariable(name = "studyId") Long studyId,
                                                                          @PathVariable(name = "scheduleId") Long scheduleId,
                                                                          @Valid @RequestBody ScheduleRequestDto.UpdateDto requestDto) {
        return ResponseEntity.ok(scheduleService.updateSchedule(studyId, scheduleId, requestDto, member));
    }

    @Operation(summary = "일정 삭제")
    @DeleteMapping("/{scheduleId}")
    ResponseEntity<Long> deleteSchedule(@CurrentMember Member member,
                                        @PathVariable(name = "studyId") Long studyId,
                                        @PathVariable(name = "scheduleId") Long scheduleId) {
        return ResponseEntity.ok(scheduleService.deleteSchedule(studyId, scheduleId, member));
    }
}
