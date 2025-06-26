package com.kustaurant.kustaurant.comment.infrastructure;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.comment.service.port.PostCommentDislikeRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.UserNotFoundException;
import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.user.user.infrastructure.UserJpaRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostCommentDislikeRepositoryImpl implements PostCommentDislikeRepository {
    private final PostCommentDislikeJpaRepository postCommentDislikeJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final PostCommentJpaRepository postCommentJpaRepository;
    @Override
    public void save(PostCommentDislike dislike) {
        UserEntity userEntity = userJpaRepository.findByUserId(dislike.getUserId())
                .orElseThrow(UserNotFoundException::new);
        PostCommentEntity commentEntity = postCommentJpaRepository.findById(dislike.getCommentId())
                .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUNT, dislike.getCommentId(), "댓글"));

        PostCommentDislikeEntity entity = toEntity(dislike, userEntity, commentEntity);
        postCommentDislikeJpaRepository.save(entity);
    }

    public PostCommentDislikeEntity toEntity(PostCommentDislike dislike, UserEntity userEntity, PostCommentEntity commentEntity) {
        return PostCommentDislikeEntity.builder()
                .user(userEntity)
                .postComment(commentEntity)
                .createdAt(dislike.getCreatedAt())
                .build();
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public List<PostCommentDislike> findByCommentId(Integer commentId) {
        return List.of();
    }

    @Override
    public Optional<PostCommentDislike> findByUserIdAndCommentId(Long userId, Integer commentId) {
        return Optional.empty();
    }

    @Override
    public boolean existsByUserIdAndCommentId(Long userId, Integer commentId) {
        return postCommentDislikeJpaRepository.existsByUser_UserIdAndPostComment_CommentId(userId, commentId);
    }

    @Override
    public void deleteByUserIdAndCommentId(Long userId, Integer commentId) {
        postCommentDislikeJpaRepository.deleteByUser_UserIdAndPostComment_CommentId(userId, commentId);
    }

    @Override
    public int countByCommentId(Integer commentId) {
        return postCommentDislikeJpaRepository.countByPostComment_CommentId(commentId);
    }
}
