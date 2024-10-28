package com.web.stard.domain.board.global.repository;

import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.domain.enums.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByPostType(PostType postType, Pageable pageable);

    Page<Post> findByPostTypeAndTitleContainingOrContentContaining(PostType postType, String keyword, String keyword1, Pageable pageable);
}