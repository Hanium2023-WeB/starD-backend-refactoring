package com.web.stard.domain.reply.repository;

import com.web.stard.domain.reply.domain.entity.Reply;
import com.web.stard.domain.member.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    @EntityGraph(attributePaths = {"member.profile"})
    Optional<Reply> findByIdAndMember(Long id, Member member);

    @EntityGraph(attributePaths = {"member.profile"})
    Page<Reply> findByTargetId(Long targetId, Pageable pageable);

    List<Reply> findAllByTargetId(Long targetId);
}
