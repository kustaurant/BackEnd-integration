package com.kustaurant.kustaurant.common.comment.infrastructure;

import com.kustaurant.kustaurant.common.comment.service.port.PostCommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostCommentLikeRepositoryImpl implements PostCommentLikeRepository {
    private final PostCommentLikeJpaRepository postCommentLikeJpaRepository;


    @Override
    public Optional<PostCommentLike> findByUserIdAndCommentId(Integer userId, Integer commentId) {
        return postCommentLikeJpaRepository.findByUser_UserIdAndPostComment_CommentId(userId, commentId)
                .map(PostCommentLikeEntity::toDomain);
    }

    @Override
    public void save(PostCommentLike postCommentLike) {

    }

    @Override
    public void deleteByUserIdAndCommentId(Integer userId, Integer commentId) {

    }

    @Override
    public boolean existsByUserIdAndCommentId(Integer userId, Integer commentId) {
        return postCommentLikeJpaRepository.existsByUser_UserIdAndPostComment_CommentId(userId, commentId);
    }
}
