package com.kustaurant.kustaurant.post.comment.infrastructure.repo;

import com.kustaurant.kustaurant.post.comment.domain.PostCommentDislike;
import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommentDislikeEntity;
import com.kustaurant.kustaurant.post.comment.infrastructure.repo.jpa.PostCommentDislikeJpaRepository;
import com.kustaurant.kustaurant.post.comment.infrastructure.repo.jpa.PostCommentJpaRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentDislikeRepository;
import com.kustaurant.kustaurant.user.user.infrastructure.UserJpaRepository;
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
        PostCommentDislikeEntity entity = PostCommentDislikeEntity.builder()
                .userId(dislike.getUserId())
                .commentId(dislike.getCommentId())
                .createdAt(dislike.getCreatedAt())
                .build();
        postCommentDislikeJpaRepository.save(entity);
    }

    @Override
    public void deleteById(Integer id) {
        postCommentDislikeJpaRepository.deleteById(id);
    }

    @Override
    public List<PostCommentDislike> findByCommentId(Integer commentId) {
        return postCommentDislikeJpaRepository.findByCommentId(commentId).stream()
                .map(PostCommentDislikeEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<PostCommentDislike> findByUserIdAndCommentId(Long userId, Integer commentId) {
        return postCommentDislikeJpaRepository.findByUserIdAndCommentId(userId, commentId)
                .map(PostCommentDislikeEntity::toDomain);
    }

    @Override
    public boolean existsByUserIdAndCommentId(Long userId, Integer commentId) {
        return postCommentDislikeJpaRepository.existsByUserIdAndCommentId(userId, commentId);
    }

    @Override
    public void deleteByUserIdAndCommentId(Long userId, Integer commentId) {
        postCommentDislikeJpaRepository.deleteByUserIdAndCommentId(userId, commentId);
    }

    @Override
    public int countByCommentId(Integer commentId) {
        return postCommentDislikeJpaRepository.countByCommentId(commentId);
    }
}
