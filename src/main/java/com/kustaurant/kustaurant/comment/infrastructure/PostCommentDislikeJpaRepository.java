package com.kustaurant.kustaurant.comment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCommentDislikeJpaRepository extends JpaRepository<PostCommentDislikeEntity,Integer> {
    Optional<PostCommentDislikeEntity> findByUser_UserIdAndPostComment_CommentId(Integer userId, Integer commentId);

    boolean existsByUser_UserIdAndPostComment_CommentId(Long userId, Integer commentId);

    int countByPostComment_CommentId(Integer commentId);

    void deleteByUser_UserIdAndPostComment_CommentId(Long userId, Integer commentId);
}
