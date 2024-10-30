package com.web.stard.domain.board.global.repository;

import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.domain.enums.Category;
import com.web.stard.domain.board.global.domain.enums.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByPostType(PostType postType, Pageable pageable);

    Page<Post> findByPostTypeAndTitleContainingOrContentContaining(PostType postType, String keyword, String keyword1, Pageable pageable);

    Page<Post> findByPostTypeAndCategory(PostType postType, Category category, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.postType = :postType" +
            " AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)" +
            " AND p.category = :category")
    Page<Post> searchCommPostWithCategory(@Param("postType") PostType postType,
                                          @Param("keyword") String keyword,
                                          @Param("category") Category category,
                                          Pageable pageable);
}
