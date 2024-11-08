package com.web.stard.domain.board.global.application;

import com.web.stard.domain.board.global.domain.enums.ActType;
import com.web.stard.domain.board.global.domain.enums.TableType;
import com.web.stard.domain.board.global.dto.response.PostResponseDto;
import com.web.stard.domain.member.domain.Member;

public interface StarScrapService {
    Long addStarScrap(Member member, Long targetId, ActType actType, TableType tableType);

    boolean deleteStarScrap(Member member, Long targetId, ActType actType, TableType tableType);

    int findStarCount(Long targetId);

    PostResponseDto.PostListDto getMemberStarPostList(Member member, int page);

    // Study 스크랩 List 조회

    // 해당 Study 공감 개수
}
