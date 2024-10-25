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
}
