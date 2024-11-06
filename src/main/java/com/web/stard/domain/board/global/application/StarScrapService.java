package com.web.stard.domain.board.global.application;

import com.web.stard.domain.board.global.domain.enums.ActType;
import com.web.stard.domain.board.global.domain.enums.TableType;
import com.web.stard.domain.member.domain.Member;

public interface StarScrapService {
    Long addStarScrap(Member member, Long targetId, ActType actType, TableType tableType);

    boolean deleteStarScrap(Member member, Long targetId, ActType actType, TableType tableType);

    // 공감한 Post(COMM, FAQ, QNA) List 조회

    // 해당 Post(COMM, FAQ, QNA)의 공감 개수


    // Study 스크랩 List 조회

    // 해당 Study 공감 개수
}
