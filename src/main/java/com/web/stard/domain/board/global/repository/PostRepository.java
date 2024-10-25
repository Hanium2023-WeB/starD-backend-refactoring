package com.web.stard.domain.board.global.repository;

import com.web.stard.domain.board.global.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
