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
}
