package com.web.stard.domain.starScrap.service;

import com.web.stard.domain.starScrap.domain.entity.StarScrap;
import com.web.stard.domain.starScrap.domain.enums.ActType;
import com.web.stard.domain.starScrap.domain.enums.TableType;
import com.web.stard.domain.post.domain.dto.response.PostResponseDto;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.study.domain.dto.response.StudyResponseDto;

public interface StarScrapService {
    Long addStarScrap(Member member, Long targetId, String type);

    boolean deleteStarScrap(Member member, Long targetId, String type);

    void deletePostStarScraps(Long targetId, ActType actType, TableType tableType);

    int findStarScrapCount(Long targetId, ActType actType, TableType tableType);

    PostResponseDto.PostListDto getMemberStarPostList(Member member, int page);

    StudyResponseDto.StudyRecruitListDto getMemberScrapStudyList(Member member, int page);

    StarScrap existsStarScrap(Member member, Long targetId, ActType actType, TableType tableType);
}
