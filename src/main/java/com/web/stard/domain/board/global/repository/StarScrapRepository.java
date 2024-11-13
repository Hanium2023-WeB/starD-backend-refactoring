package com.web.stard.domain.board.global.repository;

import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.domain.StarScrap;
import com.web.stard.domain.board.global.domain.enums.ActType;
import com.web.stard.domain.board.global.domain.enums.TableType;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.study.domain.entity.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StarScrapRepository extends JpaRepository<StarScrap, Long> {
    Optional<StarScrap> findByMemberAndActTypeAndTableTypeAndTargetId(Member member, ActType actType, TableType tableType, Long targetId);

    List<StarScrap> findAllByActTypeAndTableTypeAndTargetId(ActType actType, TableType tableType, Long targetId);

    @Query("SELECT p FROM Post p JOIN StarScrap s ON p.id = s.targetId WHERE s.member = :member AND s.actType = 'STAR'")
    Page<Post> findPostsByMember(Member member, Pageable pageable);

    @Query("SELECT st FROM Study st JOIN StarScrap ss ON st.id = ss.targetId WHERE ss.member = :member AND ss.actType = 'SCRAP'")
    Page<Study> findStudyRecruitPostsByMember(Member member, Pageable pageable);
}
