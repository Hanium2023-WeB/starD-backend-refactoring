package com.web.stard.domain.admin.api;

import com.web.stard.domain.admin.application.NoticeService;
import com.web.stard.domain.admin.dto.request.NoticeRequestDto;
import com.web.stard.domain.admin.dto.response.NoticeResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
