package com.web.stard.domain.board.global.repository;

import com.web.stard.domain.board.global.domain.StarScrap;
import com.web.stard.domain.board.global.domain.enums.ActType;
import com.web.stard.domain.board.global.domain.enums.TableType;
import com.web.stard.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StarScrapRepository extends JpaRepository<StarScrap, Long> {
    Optional<StarScrap> findByMemberAndActTypeAndTableTypeAndTargetId(Member member, ActType actType, TableType tableType, Long targetId);

    List<StarScrap> findAllByActTypeAndTableTypeAndTargetId(ActType actType, TableType tableType, Long targetId);
}
