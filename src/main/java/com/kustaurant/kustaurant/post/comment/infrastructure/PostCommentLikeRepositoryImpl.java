package com.kustaurant.kustaurant.post.comment.infrastructure;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.post.comment.service.port.PostCommentLikeRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostCommentLikeRepositoryImpl implements PostCommentLikeRepository {
    private final PostCommentLikeJpaRepository postCommentLikeJpaRepository;
    private final PostCommentJpaRepository postCommentJpaRepository;
    @Override
    public Optional<PostCommentLike> findByUserIdAndCommentId(Long userId, Integer commentId) {
        return postCommentLikeJpaRepository.findByUserIdAndPostComment_CommentId(userId, commentId)
                .map(PostCommentLikeEntity::toDomain);
    }

    public void save(PostCommentLike like) {
        PostCommentEntity commentEntity = postCommentJpaRepository.findById(like.getCommentId())
                .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUNT, like.getCommentId(), "댓글"));

        PostCommentLikeEntity entity = toEntity(like, like.getUserId(), commentEntity);
        postCommentLikeJpaRepository.save(entity);
    }

    public PostCommentLikeEntity toEntity(PostCommentLike like, Long userId, PostCommentEntity commentEntity) {
        return PostCommentLikeEntity.builder()
                .userId(userId)
                .postComment(commentEntity)
                .createdAt(like.getCreatedAt())
                .build();
    }

    @Override
    public void deleteByUserIdAndCommentId(Long userId, Integer commentId) {
        postCommentLikeJpaRepository.deleteByUserIdAndPostComment_CommentId(userId, commentId);
    }


    @Override
    public boolean existsByUserIdAndCommentId(Long userId, Integer commentId) {
        return postCommentLikeJpaRepository.existsByUserIdAndPostComment_CommentId(userId, commentId);
    }

    @Override
    public int countByCommentId(Integer commentId) {
        return postCommentLikeJpaRepository.countByPostComment_CommentId(commentId);
    }

}
