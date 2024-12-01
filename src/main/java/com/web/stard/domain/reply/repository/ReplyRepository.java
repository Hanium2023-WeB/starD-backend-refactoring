package com.web.stard.domain.reply.repository;

import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.reply.domain.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    @EntityGraph(attributePaths = {"member.profile"})
    Page<Reply> findByTargetIdAndPostType(Long targetId, PostType postType, Pageable pageable);

    List<Reply> findAllByTargetIdAndPostType(Long postId, PostType postType);

    void deleteAllByTargetIdAndPostType(Long targetId, PostType type);
}
