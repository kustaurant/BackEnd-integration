package com.kustaurant.kustaurant.comment.infrastructure;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.comment.service.port.PostCommentLikeRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.UserNotFoundException;
import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.user.user.infrastructure.UserJpaRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostCommentLikeRepositoryImpl implements PostCommentLikeRepository {
    private final PostCommentLikeJpaRepository postCommentLikeJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final PostCommentJpaRepository postCommentJpaRepository;
    @Override
    public Optional<PostCommentLike> findByUserIdAndCommentId(Long userId, Integer commentId) {
        return postCommentLikeJpaRepository.findByUser_UserIdAndPostComment_CommentId(userId, commentId)
                .map(PostCommentLikeEntity::toDomain);
    }

    public void save(PostCommentLike like) {
        UserEntity userEntity = userJpaRepository.findByUserId(like.getUserId())
                .orElseThrow(UserNotFoundException::new);
        PostCommentEntity commentEntity = postCommentJpaRepository.findById(like.getCommentId())
                .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUNT, like.getCommentId(), "댓글"));

        PostCommentLikeEntity entity = toEntity(like, userEntity, commentEntity);
        postCommentLikeJpaRepository.save(entity);
    }

    public PostCommentLikeEntity toEntity(PostCommentLike like, UserEntity userEntity, PostCommentEntity commentEntity) {
        return PostCommentLikeEntity.builder()
                .user(userEntity)
                .postComment(commentEntity)
                .createdAt(like.getCreatedAt())
                .build();
    }

    @Override
    public void deleteByUserIdAndCommentId(Long userId, Integer commentId) {
        postCommentLikeJpaRepository.deleteByUser_UserIdAndPostComment_CommentId(userId, commentId);
    }


    @Override
    public boolean existsByUserIdAndCommentId(Long userId, Integer commentId) {
        return postCommentLikeJpaRepository.existsByUser_UserIdAndPostComment_CommentId(userId, commentId);
    }

    @Override
    public int countByCommentId(Integer commentId) {
        return postCommentLikeJpaRepository.countByPostComment_CommentId(commentId);
    }

}
