package com.kustaurant.kustaurant.comment.infrastructure;

import com.kustaurant.kustaurant.comment.service.port.PostCommentLikeRepository;
import com.kustaurant.kustaurant.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.user.infrastructure.UserJpaRepository;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
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
    public Optional<PostCommentLike> findByUserIdAndCommentId(Integer userId, Integer commentId) {
        return postCommentLikeJpaRepository.findByUser_UserIdAndPostComment_CommentId(userId, commentId)
                .map(PostCommentLikeEntity::toDomain);
    }

    public void save(PostCommentLike like) {
        UserEntity userEntity = userJpaRepository.findByUserId(like.getUserId())
                .orElseThrow(() -> new DataNotFoundException("유저 없음"));
        PostCommentEntity commentEntity = postCommentJpaRepository.findById(like.getCommentId())
                .orElseThrow(() -> new DataNotFoundException("댓글 없음"));

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
    public void deleteByUserIdAndCommentId(Integer userId, Integer commentId) {
        postCommentLikeJpaRepository.deleteByUser_UserIdAndPostComment_CommentId(userId, commentId);
    }


    @Override
    public boolean existsByUserIdAndCommentId(Integer userId, Integer commentId) {
        return postCommentLikeJpaRepository.existsByUser_UserIdAndPostComment_CommentId(userId, commentId);
    }

    @Override
    public int countByCommentId(Integer commentId) {
        return postCommentLikeJpaRepository.countByPostComment_CommentId(commentId);
    }

}
