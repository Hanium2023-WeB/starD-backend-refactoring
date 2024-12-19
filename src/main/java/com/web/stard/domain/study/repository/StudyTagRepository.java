package com.web.stard.domain.study.repository;

import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyTag;
import com.web.stard.domain.study.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StudyTagRepository extends JpaRepository<StudyTag, Long> {

    @Query("SELECT st.tag FROM StudyTag st where st.study = :study")
    List<Tag> findByStudy(@Param("study") Study study);

    void deleteByStudyAndTagIn(Study study, List<Tag> tags);

    @Query("SELECT st.tag as cnt FROM StudyTag st " +
            "WHERE st.createdAt >= :start " +
            "group by st.tag.id " +
            "order by count(*) desc " +
            "limit 5")
    List<Tag> tagsByStudy(@Param("start") LocalDateTime start);

}
