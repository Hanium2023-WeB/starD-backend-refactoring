package com.web.stard.domain.teamBlog.api;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.teamBlog.domain.dto.request.ScheduleRequestDto;
import com.web.stard.domain.teamBlog.domain.dto.response.ScheduleResponseDto;
import com.web.stard.domain.teamBlog.service.ScheduleService;
import com.web.stard.global.domain.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/studies/{studyId}/schedules")
@RequiredArgsConstructor
@Tag(name = "studies-schedules", description = "스터디 팀블로그 - 일정 관련 API")
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
    public ResponseEntity<Long> deleteSchedule(@CurrentMember Member member,
                                               @PathVariable(name = "studyId") Long studyId,
                                               @PathVariable(name = "scheduleId") Long scheduleId) {
        return ResponseEntity.ok(scheduleService.deleteSchedule(studyId, scheduleId, member));
    }

    @Operation(summary = "스터디 별 일정 조회")
    @GetMapping
    public ResponseEntity<List<ScheduleResponseDto.ScheduleDto>> getStudyScheduleList(@CurrentMember Member member,
                                                                                      @PathVariable(name = "studyId") Long studyId,
                                                                                      @RequestParam(name = "year") int year,
                                                                                      @RequestParam(name = "month") int month) {
        return ResponseEntity.ok(scheduleService.getAllScheduleListByStudy(studyId, member, year, month));
    }
}
