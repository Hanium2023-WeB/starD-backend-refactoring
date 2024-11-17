package com.web.stard.domain.board.global.service;

import com.web.stard.domain.board.global.domain.enums.ActType;
import com.web.stard.domain.board.global.domain.enums.TableType;
import com.web.stard.domain.board.global.dto.response.PostResponseDto;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.study.domain.dto.StudyResponseDto;

public interface StarScrapService {
    Long addStarScrap(Member member, Long targetId, ActType actType, TableType tableType);

    boolean deleteStarScrap(Member member, Long targetId, ActType actType, TableType tableType);

    int findStarScrapCount(Long targetId, ActType actType, TableType tableType);

    PostResponseDto.PostListDto getMemberStarPostList(Member member, int page);

    StudyResponseDto.StudyRecruitListDto getMemberScrapStudyList(Member member, int page);
}
