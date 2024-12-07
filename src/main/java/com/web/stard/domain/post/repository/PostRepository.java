package com.web.stard.domain.post.repository;

import com.web.stard.domain.post.domain.entity.Post;
import com.web.stard.domain.post.domain.enums.Category;
import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.member.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    @EntityGraph(attributePaths = {"member.profile"})
    Optional<Post> findByIdAndPostType(Long postId, PostType postType);

    @EntityGraph(attributePaths = {"member.profile"})
    List<Post> findByPostTypeInOrderByPostTypeAscCreatedAtDesc(List<PostType> list);

    @EntityGraph(attributePaths = {"member.profile"})
    @Query("SELECT p FROM Post p WHERE p.postType IN :postTypes AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) " +
            "ORDER BY CASE p.postType " +
            "WHEN :faq THEN 1 WHEN :qna THEN 2 END, p.createdAt DESC")
    List<Post> findByPostTypeInAndTitleOrContentContaining(
            @Param("postTypes") List<PostType> postTypes, @Param("keyword") String keyword,
            @Param("faq") PostType faq, @Param("qna") PostType qna);

    Page<Post> findByMemberAndPostType(Member member, PostType postType, Pageable pageable);

    List<Post> findByMember(Member member);

    void deleteAllByMember(Member member);
}
