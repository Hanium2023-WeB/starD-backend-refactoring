package com.web.stard.domain.board.global.repository;

import com.web.stard.domain.board.global.domain.Reply;
import com.web.stard.domain.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    @EntityGraph(attributePaths = {"member.profile"})
    Optional<Reply> findByIdAndMember(Long id, Member member);

    @EntityGraph(attributePaths = {"member.profile"})
    Page<Reply> findByTargetId(Long targetId, Pageable pageable);
}
