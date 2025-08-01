package com.kustaurant.kustaurant.post.comment.infrastructure.repo;

import com.kustaurant.kustaurant.post.comment.domain.PostCommentLike;
import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommentLikeEntity;
import com.kustaurant.kustaurant.post.comment.infrastructure.repo.jpa.PostCommentLikeJpaRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostCommentLikeRepositoryImpl implements PostCommentLikeRepository {
    private final PostCommentLikeJpaRepository postCommentLikeJpaRepository;
    
    @Override
    public Optional<PostCommentLike> findByUserIdAndCommentId(Long userId, Integer commentId) {
        return postCommentLikeJpaRepository.findByUserIdAndCommentId(userId, commentId)
                .map(PostCommentLikeEntity::toDomain);
    }

    public void save(PostCommentLike like) {
        PostCommentLikeEntity entity = PostCommentLikeEntity.builder()
                .userId(like.getUserId())
                .commentId(like.getCommentId())
                .createdAt(like.getCreatedAt())
                .build();
        postCommentLikeJpaRepository.save(entity);
    }

    @Override
    public void deleteByUserIdAndCommentId(Long userId, Integer commentId) {
        postCommentLikeJpaRepository.deleteByUserIdAndCommentId(userId, commentId);
    }

    @Override
    public boolean existsByUserIdAndCommentId(Long userId, Integer commentId) {
        return postCommentLikeJpaRepository.existsByUserIdAndCommentId(userId, commentId);
    }

    @Override
    public int countByCommentId(Integer commentId) {
        return postCommentLikeJpaRepository.countByCommentId(commentId);
    }

}
