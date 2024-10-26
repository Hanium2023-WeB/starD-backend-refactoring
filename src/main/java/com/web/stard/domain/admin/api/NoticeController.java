package com.web.stard.domain.admin.api;

import com.web.stard.domain.admin.application.NoticeService;
import com.web.stard.domain.admin.dto.request.NoticeRequestDto;
import com.web.stard.domain.admin.dto.response.NoticeResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "공지사항 등록", description = "관리자만 작성 가능합니다.")
    @PostMapping
    public ResponseEntity<NoticeResponseDto.NoticeDto> createNotice(@Valid @RequestBody NoticeRequestDto.CreateNoticeDto createNoticeDto) {
        return ResponseEntity.ok(noticeService.createNotice(createNoticeDto));
    }

    @Operation(summary = "공지사항 수정", description = "관리자만 수정 가능합니다.")
    @PutMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDto.NoticeDto> updateNotice(@PathVariable(name = "noticeId") Long noticeId,
                                                                    @Valid @RequestBody NoticeRequestDto.CreateNoticeDto createNoticeDto) {
        return ResponseEntity.ok(noticeService.updateNotice(noticeId, createNoticeDto));
    }

    @Operation(summary = "공지사항 삭제", description = "관리자만 삭제 가능합니다.")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Long> deleteNotice(@PathVariable(name = "noticeId") Long noticeId) {
        return ResponseEntity.ok(noticeService.deleteNotice(noticeId));
    }

    @Operation(summary = "공지사항 목록 조회")
    @GetMapping
    public ResponseEntity<NoticeResponseDto.NoticeListDto> getNoticeList(@RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(noticeService.getNoticeList(page));
    }

    @Operation(summary = "공지사항 상세조회")
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDto.NoticeDto> getNoticeDetail(@PathVariable(name = "noticeId") Long noticeId) {
        return ResponseEntity.ok(noticeService.getNoticeDetail(noticeId));
    }
}
