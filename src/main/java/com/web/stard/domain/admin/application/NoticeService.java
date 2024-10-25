package com.web.stard.domain.admin.application;

import com.web.stard.domain.admin.dto.request.NoticeRequestDto;
import com.web.stard.domain.admin.dto.response.NoticeResponseDto;

public interface NoticeService {
    NoticeResponseDto.NoticeDto createNotice(NoticeRequestDto.CreateNoticeDto createNoticeDto);

    NoticeResponseDto.NoticeDto updateNotice(Long noticeId, NoticeRequestDto.CreateNoticeDto createNoticeDto);
}
