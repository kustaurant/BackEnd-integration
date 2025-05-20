package com.kustaurant.kustaurant.common.comment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCommentLikeJpaRepository extends JpaRepository<PostCommentLikeEntity, Integer> {
    Optional<PostCommentLikeEntity> findByUser_UserIdAndPostComment_CommentId(Integer userId, Integer commentId);

    boolean existsByUser_UserIdAndPostComment_CommentId(Integer userId, Integer commentId);
}
