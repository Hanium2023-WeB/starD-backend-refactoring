package com.web.stard.domain.starScrap.repository;

import com.web.stard.domain.post.domain.entity.Post;
import com.web.stard.domain.starScrap.domain.entity.StarScrap;
import com.web.stard.domain.starScrap.domain.enums.ActType;
import com.web.stard.domain.starScrap.domain.enums.TableType;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.study.domain.entity.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StarScrapRepository extends JpaRepository<StarScrap, Long> {

    @Query("SELECT study FROM Study study JOIN StarScrap scrap ON study.id = scrap.targetId WHERE scrap.member.id = :memberId AND scrap.actType = 'SCRAP' AND scrap.tableType = 'STUDY'")
    List<Study> findStudiesByMember(@Param("memberId") Long memberId);

    Optional<StarScrap> findByMemberAndActTypeAndTableTypeAndTargetId(Member member, ActType actType, TableType tableType, Long targetId);

    List<StarScrap> findAllByActTypeAndTableTypeAndTargetId(ActType actType, TableType tableType, Long targetId);

    @Query("SELECT p FROM Post p JOIN StarScrap s ON p.id = s.targetId WHERE s.member = :member AND s.actType = 'STAR'")
    Page<Post> findPostsByMember(Member member, Pageable pageable);

    @Query("SELECT st FROM Study st JOIN StarScrap ss ON st.id = ss.targetId WHERE ss.member = :member AND ss.actType = 'SCRAP'")
    Page<Study> findStudyRecruitPostsByMember(Member member, Pageable pageable);

    void deleteByActTypeAndTableTypeAndTargetId(ActType actType, TableType tableType, Long id);

    void deleteAllByMember(Member member);

    void deleteAllByTargetIdInAndTableType(List<Long> postIds, TableType tableType);

    void deleteAllByTargetIdAndTableType(Long postIds, TableType tableType);
}
